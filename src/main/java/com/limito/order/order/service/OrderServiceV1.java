package com.limito.order.order.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.limito.common.exception.AppException;
import com.limito.order.cart.service.CartServiceV1;
import com.limito.order.common.OrderStatus;
import com.limito.order.common.feignclient.LimitedFeignClient;
import com.limito.order.common.feignclient.ResellFeignClient;
import com.limito.order.order.domain.dto.feignclient.limited.ReserveStockItemRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.dto.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignclient.resell.dto.response.InternalResponse;
import com.limito.order.order.domain.dto.request.AddOrdererRequestV1;
import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.request.CreateResellOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderResponseV1;
import com.limito.order.order.domain.dto.response.GetOrderForUserResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForCompanyResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForUserResponseV1;
import com.limito.order.order.domain.mapper.OrderMapper;
import com.limito.order.order.domain.model.CompanyOrder;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;
import com.limito.order.order.domain.repository.OrderRepositoryV1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceV1 {
	private final OrderRepositoryV1 orderRepository;
	private final OrderMapper orderMapper;
	private final ResellFeignClient resellFeignClient;
	private final LimitedFeignClient limitedFeignClient;
	private final CartServiceV1 cartService;

	// 한정판매 주문서 생성
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

		// 생성된 주문 엔티티 저장
		orderRepository.save(order);

		return orderMapper.toLimitedOrderResponse(order);
	}

	// 한정판매 주문자 정보 추가
	@Transactional
	public CreateLimitedOrderResponseV1 addLimitedOrdererData(Long userId, UUID orderId,
		AddOrdererRequestV1 ordererRequest) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

		// 주문자 정보 추가
		order.attachOrderer(ordererRequest);

		List<OrderItem> orderItems = order.deliverOrderItems();

		// 재고 예약 요청
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

		return orderMapper.toLimitedOrderResponse(order);
	}

	// 리셀 주문서 생성
	@Transactional
	public CreateResellOrderResponseV1 createResellOrderSheet(Long userId,
		CreateResellOrderRequestV1 createResellOrderRequest) {

		// Todo. 합산 가격 검증 (더블 체크)
		Order order = orderMapper.toOrderEntity(userId, createResellOrderRequest);
		order.attachSummary(createResellOrderRequest);

		List<OrderItem> orderItems = orderMapper.toOrderItemEntity(createResellOrderRequest);
		order.attachOrderItems(orderItems);

		orderRepository.save(order);

		return orderMapper.toResellOrderResponse(order);
	}

	// 리셀 주문자 정보 추가
	@Transactional
	public CreateResellOrderResponseV1 addResellOrdererData(Long userId, UUID orderId,
		AddOrdererRequestV1 ordererRequest) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

		// 주문자 정보 추가
		order.attachOrderer(ordererRequest);

		// 상품 feign : 임시 재고 예약
		List<UUID> stockIds = order.getStockIds(order);
		ResponseEntity<InternalResponse> feignReponse = resellFeignClient.reserveStock(stockIds);
		if (!feignReponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "리셀 임시 재고 예약에 실패했습니다.");
		}

		return orderMapper.toResellOrderResponse(order);
	}

	public Slice<GetOrdersForUserResponseV1> getOrdersForUser(Long userId, String userRole, Pageable pageable) {
		validateRole(userRole, "USER");

		Slice<Order> orders = orderRepository.findAllByUserIdAndOrderStatusNotOrderBySuccessedAtDesc(
			userId,
			OrderStatus.ORDER_PENDING,
			pageable
		);
		return orders.map(orderMapper::toGetOrdersForUserResponse);
	}

	public Slice<GetOrdersForCompanyResponseV1> getOrdersForCompany(Long userId, String userRole, Pageable pageable) {
		validateRole(userRole, "COMPANY");

		Slice<CompanyOrder> companyOrders =
			orderRepository.findAllBySellerIdAndOrderStatusNot(userId, OrderStatus.ORDER_PENDING, pageable);

		return companyOrders.map(orderMapper::toGetOrdersForCompanyResponse);
	}

	@Transactional(readOnly = true)
	public GetOrderForUserResponseV1 getOrderForUser(Long userId, String userRole, UUID orderId) {
		validateRole(userRole, "USER");

		Order order = orderRepository.findByIdAndUserIdAndOrderStatusNot(orderId, userId, OrderStatus.ORDER_PENDING)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."));

		return orderMapper.toGetOrderForUserResponse(order);
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
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "한정판매 임시 재고 예약에 실패했습니다");
		}
	}

	private void validateRole(String userRole, String... roles) {
		if (
			!Arrays.asList(roles)
				.contains(userRole)
		) {
			throw AppException.of(HttpStatus.FORBIDDEN, "조회 권한이 없습니다.");
		}
	}
}
