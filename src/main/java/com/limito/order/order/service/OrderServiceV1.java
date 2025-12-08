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
import com.limito.order.common.feignClient.LimitedFeignClient;
import com.limito.order.common.feignClient.ResellFeignClient;
import com.limito.order.order.domain.dto.feignClient.limited.ReduceStockProductRequestV1;
import com.limito.order.order.domain.dto.feignClient.limited.ReduceStockRequestV1;
import com.limito.order.order.domain.dto.feignClient.limited.ReserveStockItemRequestV1;
import com.limito.order.order.domain.dto.feignClient.limited.ReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignClient.resell.dto.request.StockReduceRequest;
import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.request.CreateResellOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderResponseV1;
import com.limito.order.order.domain.mapper.OrderMapper;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;
import com.limito.order.order.domain.repository.OrderRepositoryV1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceV1 {
	private final OrderRepositoryV1 orderRepository;
	private final OrderMapper orderMapper;
	private final ResellFeignClient resellFeignClient;
	private final LimitedFeignClient limitedFeignClient;

	// 한정판매 주문 생성
	@Transactional
	public CreateLimitedOrderResponseV1 createLimitedOrder(Long userId,
		CreateLimitedOrderRequestV1 createLimitedOrderRequest) {

		// Todo. 합산 가격 검증 (더블 체크)

		// requestDto -> entity 매핑해서 주문 엔티티 생성
		Order order = orderMapper.toOrderEntity(userId, createLimitedOrderRequest);

		// requestDto -> entity 매핑해서 주문 아이템 엔티티 생성 후 orderItems로 반환
		List<OrderItem> orderItems = orderMapper.toOrderItemEntity(createLimitedOrderRequest);
		// 연관관계 설정
		order.attachOrderItems(orderItems);

		// itemSummary set 하는 함수 추가하기
		order.attachSummary(createLimitedOrderRequest);

		// feign requestDto 생성
		ReserveStockRequestV1 reserveStockRequest = new ReserveStockRequestV1();
		List<ReserveStockItemRequestV1> reserveStockItemRequests = new ArrayList<>();

		for (OrderItem orderItem : orderItems) {
			UUID productItemId = orderItem.getProductItemId();
			int amount = orderItem.getProductAmount();

			ReserveStockItemRequestV1 reserveStockItemRequest = ReserveStockItemRequestV1.of(productItemId, amount);

			reserveStockItemRequests.add(reserveStockItemRequest);
		}

		if (reserveStockItemRequests.isEmpty()) {
			throw AppException.of(HttpStatus.NO_CONTENT, "reserveStockItemRequests 비어있습니다.");
		}

		reserveStockRequest.attachItems(reserveStockItemRequests);

		// feign 요청
		ResponseEntity<Void> reserveFeignResponse = limitedFeignClient.reserveStock(reserveStockRequest);
		validateReserveFeign(reserveFeignResponse);

		// 생성된 주문 엔티티 저장
		orderRepository.save(order);

		return orderMapper.toLimitedOrderResponse(order);
	}

	@Transactional
	public void afterPayments(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

		// 상품 feign: 재고 차감 요청
		List<ReduceStockProductRequestV1> reduceProductRequests = new ArrayList<>();

		List<OrderItem> orderItems = order.deliverOrderItems();
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
		order.changeStatus(OrderStatus.ORDER_FINISH);

		// 주문 상품 장바구니에서 차감
		
	}

	// 리셀 주문 생성

	/** Todo :
	 * 1. 상품 feign : 임시 재고 예약
	 * 3. 상품 feign: 재고 차감 요청
	 * 4. 주문 상품 장바구니에서 차감
	 */
	@Transactional
	public CreateResellOrderResponseV1 createResellOrder(Long userId,
		CreateResellOrderRequestV1 createResellOrderRequest) {

		// Todo. 합산 가격 검증 (더블 체크)

		Order order = orderMapper.toOrderEntity(userId, createResellOrderRequest);
		List<OrderItem> orderItems = orderMapper.toOrderItemEntity(createResellOrderRequest);
		order.attachOrderItems(orderItems);

		order.attachSummary(createResellOrderRequest);

		// 상품 feign : 임시 재고 예약
		// Todo. 예외처리
		List<UUID> stockIds = order.getStockIds(order);
		resellFeignClient.reserveStock(stockIds);

		// 상품 feign: 재고 차감 요청
		// Todo. 예외처리
		List<StockReduceRequest> reduceRequests = createStockReduceRequests(orderItems);
		resellFeignClient.reduceStock(reduceRequests);

		orderRepository.save(order);

		CreateResellOrderResponseV1 rsellOrderRes = orderMapper.toResellOrderResponse(order);
		return rsellOrderRes;
	}

	private List<StockReduceRequest> createStockReduceRequests(List<OrderItem> orderItems) {
		List<StockReduceRequest> requests = new ArrayList<>();
		orderItems.forEach(orderItem -> {
			StockReduceRequest req = StockReduceRequest.createRequest(orderItem);
			requests.add(req);
		});
		return requests;
	}

	private void validateReserveFeign(ResponseEntity<Void> reserveFeignResponse) {
		if (!HttpStatus.OK.equals(reserveFeignResponse.getStatusCode())) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "임시 재고 예약에 실패했습니다");
			// Todo : 예외처리 강화 - feign 응답에 맞춰서
		}
	}
}