package com.limito.order.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.limito.common.exception.AppException;
import com.limito.order.common.OrderStatus;
import com.limito.order.common.feignclient.LimitedFeignClient;
import com.limito.order.common.feignclient.ResellFeignClient;
import com.limito.order.order.domain.dto.feignclient.limited.CancelReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReduceStockProductRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReduceStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.RollbackStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignclient.resell.request.StockRollbackRequest;
import com.limito.order.order.domain.dto.feignclient.resell.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignclient.limited.RollbackStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.dto.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignclient.resell.dto.request.StockRollbackRequest;
import com.limito.order.order.domain.mapper.OrderMapper;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;
import com.limito.order.order.domain.repository.OrderRepositoryV1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderInternalServiceV1 {
	private final OrderRepositoryV1 orderRepository;
	private final OrderMapper orderMapper;
	private final ResellFeignClient resellFeignClient;
	private final LimitedFeignClient limitedFeignClient;

	// 한정판매 주문 완료
	@Transactional
	public void limitedOrderUpdate(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

		// 상품 feign: 재고 차감 요청
		List<ReduceStockProductRequestV1> reduceProductRequests = new ArrayList<>();

		List<OrderItem> orderItems = order.getOrderItems();
		for (OrderItem orderItem : orderItems) {
			ReduceStockProductRequestV1 req = new ReduceStockProductRequestV1(orderItem.getOptionId(),
				orderItem.getProductItemId(), orderItem.getProductAmount());
			reduceProductRequests.add(req);
		}
		ReduceStockRequestV1 reduceRequest = new ReduceStockRequestV1(reduceProductRequests);

		ResponseEntity<Void> feinResponse = limitedFeignClient.reduceStock(reduceRequest);
		if (!feinResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "재고 차감 요청에 실패해습니다.");
		}

		// 주문 상태 변경
		order.attachSuccess();
		order.changeStatus(OrderStatus.ORDER_FINISH);
	}

	// 리셀 주문 완료
	@Transactional
	public void resellOrderFinish(UUID orderId) {

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));
		List<OrderItem> orderItems = order.getOrderItems();

		// 상품 feign: 재고 차감 요청
		// Todo. 예외처리
		List<StockReduceRequest> reduceRequests = createStockReduceRequests(orderItems);
		ResponseEntity<Void> feignResponse = resellFeignClient.reduceStock(reduceRequests);
		if (!feignResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "리셀 재고 차감 요청에 실패했습니다.");
		}

		// 주문 상태 변경
		order.attachSuccess();
		order.changeStatus(OrderStatus.ORDER_FINISH);
	}

	// 한정판매 결제 실패
	@Transactional
	public void limitedOrderFail(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));
		List<OrderItem> orderItems = order.getOrderItems();

		// 임시 재고 예약 취소 요청
		CancelReserveStockRequestV1 feignRequest = orderMapper.reserveCancelRequest(orderItems);
		ResponseEntity<Void> feignResponse = limitedFeignClient.cancelReserveStock(feignRequest);
		log.info("페인 클라이언트 상태 코드 : {}", feignResponse.getStatusCode());

		// 예외처리
		if (!feignResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "한정판매 임시 재고 예약 취소에 실패했습니다.");
		}

		// 주문 삭제
		order.softDelete();
		order.changeStatus(OrderStatus.ORDER_FAIL);
	}

	// 리셀 결제 실패
	@Transactional
	public void resellOrderFail(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));
		List<OrderItem> orderItems = order.getOrderItems();

		// 임시 재고 예약 취소 요청
		List<UUID> stockIds = orderMapper.getStockIds(orderItems);
		// 현서님 exception 수정 되면 변경
		ResponseEntity<Void> feignResponse = resellFeignClient.cancelStock(stockIds);

		// 예외처리
		if (!feignResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "한정판매 임시 재고 예약 취소에 실패했습니다.");
		}

		// 주문 삭제
		order.softDelete();
		order.changeStatus(OrderStatus.ORDER_FAIL);
	}

	// 한정판매 주문 취소
	@Transactional
	public void limitedOrderCancel(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));
		List<OrderItem> orderItems = order.getOrderItems();

		// 한정판매 feign : 재고 복원
		RollbackStockRequestV1 feignRequest = orderMapper.toRollbackStockRequest(orderItems);
		ResponseEntity<Void> feignResponse = limitedFeignClient.rollbackStock(feignRequest);
		if (!feignResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "한정판매 재고 복원 요청에 실패했습니다.");
		}

		//주문 상태 변경
		order.changeStatus(OrderStatus.ORDER_CANCEL);
	}

	// 리셀 주문 취소
	@Transactional
	public void resellOrderCancel(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));
		List<OrderItem> orderItems = order.getOrderItems();

		// 리셀 feign : 재고 복원
		List<StockRollbackRequest> feignRequest = orderMapper.toStockRollbackRequest(orderItems);
		ResponseEntity<Void> feignResponse = resellFeignClient.rollbackStock(feignRequest);
		if (!feignResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "리셀 재고 복원 요청에 실패했습니다.");
		}

		//주문 상태 변경
		order.changeStatus(OrderStatus.ORDER_CANCEL);
	}

	private List<StockReduceRequest> createStockReduceRequests(List<OrderItem> orderItems) {
		List<StockReduceRequest> requests = new ArrayList<>();
		orderItems.forEach(orderItem -> {
			StockReduceRequest req = StockReduceRequest.createRequest(orderItem);
			requests.add(req);
		});
		return requests;
	}
}
