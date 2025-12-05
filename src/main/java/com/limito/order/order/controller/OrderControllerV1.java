package com.limito.order.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.service.OrderServiceV1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderControllerV1 {
	private final OrderServiceV1 orderService;

	@PostMapping("/limited")
	public ResponseEntity<CreateLimitedOrderResponseV1> createDirectLimitedOrder(
		@Valid @RequestBody CreateLimitedOrderRequestV1 createLimitedOrderRequest) {
		// Todo. userId 헤더에서 빼오기, 권한검증
		Long userId = 1111L;
		CreateLimitedOrderResponseV1 result = orderService.createDirectLimitedOrder(userId, createLimitedOrderRequest);
		return ResponseEntity.ok(result);
	}
}
