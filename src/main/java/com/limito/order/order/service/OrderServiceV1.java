package com.limito.order.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.request.CreateResellOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderResponseV1;
import com.limito.order.order.domain.mapper.OrderMapper;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;
import com.limito.order.order.domain.repository.OrderRepositoryV1;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {
	private final OrderRepositoryV1 orderRepository;
	private final OrderMapper orderMapper;

	// 한정판매 주문 생성
	@Transactional
	public CreateLimitedOrderResponseV1 createDirectLimitedOrder(Long userId,
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

		/** Todo :
		 * 1. 상품 feign : 임시 재고 예약 (최대 구매 가능 수량 포함)
		 * 2. 결제 feign : 결제 요청
		 * 3. 상품 feign: 재고 차감 요청
		 * 4. 주문 상품 장바구니에서 차감
		 */

		// 엔티티 -> responseDto 변환해서 리턴
		//		List<CreateLimitedOrderItemResponseV1> orderItemRes = orderMapper.toLimitedOrderItemResponse(order);
		CreateLimitedOrderResponseV1 limitedOrderRes = orderMapper.toLimitedOrderResponse(order);

		return limitedOrderRes;
	}

	// 리셀 주문 생성
	@Transactional
	public CreateResellOrderResponseV1 createResellOrder(Long userId,
		CreateResellOrderRequestV1 createResellOrderRequest) {

		// Todo. 합산 가격 검증 (더블 체크)

		Order order = orderMapper.toOrderEntity(userId, createResellOrderRequest);
		List<OrderItem> orderItems = orderMapper.toOrderItemEntity(createResellOrderRequest);
		order.attachOrderItems(orderItems);

		order.attachSummary(createResellOrderRequest);

		orderRepository.save(order);

		/** Todo :
		 * 1. 상품 feign : 임시 재고 예약 (최대 구매 가능 수량 포함)
		 * 2. 결제 feign : 결제 요청
		 * 3. 상품 feign: 재고 차감 요청
		 * 4. 주문 상품 장바구니에서 차감
		 */

		// 엔티티 -> responseDto 변환해서 리턴
		//		List<CreateLimitedOrderItemResponseV1> orderItemRes = orderMapper.toLimitedOrderItemResponse(order);
		CreateResellOrderResponseV1 rsellOrderRes = orderMapper.toResellOrderResponse(order);

		return rsellOrderRes;
	}
}