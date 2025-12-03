package com.limito.order.order.service;

import org.springframework.stereotype.Service;

import com.limito.order.order.domain.dto.request.CreateLimitedOrderResquestV1;
import com.limito.order.order.domain.dto.response.LimitedOrderResponseV1;
import com.limito.order.order.domain.repository.OrderServiceRepositoryV1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {
	private final OrderServiceRepositoryV1 orderServiceRepository;

	public LimitedOrderResponseV1 createDirectLimitedOrder(Long userId,
		@Valid CreateLimitedOrderResquestV1 createLimitedOrderResquest) {
		//requestDto -> entity 매핑해서 주문 엔티티 생성

		// 생성된 주문 엔티티 저장

		// 엔티티 -> responseDto 변환해서 리턴

	}
}
