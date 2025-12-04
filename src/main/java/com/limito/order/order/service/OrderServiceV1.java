package com.limito.order.order.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.limito.order.order.domain.dto.request.CreateLimitedOrderResquestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;
import com.limito.order.order.domain.repository.OrderServiceRepositoryV1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {
	private final OrderServiceRepositoryV1 orderServiceRepository;

	public CreateLimitedOrderResponseV1 createDirectLimitedOrder(Long userId,
		@Valid CreateLimitedOrderResquestV1 createLimitedOrderResquest) {

		//requestDto -> entity 매핑해서 주문 엔티티 생성
		Order order = Order.toEntity(createLimitedOrderResquest);

		// itemSummary set 하는 함수 추가하기

		//requestDto -> entity 매핑해서 주문 아이템 엔티티 생성
		List<OrderItem> orderItems = OrderItem.toEntity(createLimitedOrderResquest);
		//주문 아이템 주문 테이블에 추가...?
		order.add(orderItems);

		// 생성된 주문 엔티티 저장
		orderServiceRepository.save(order);

		// 엔티티 -> responseDto 변환해서 리턴

	}
}
