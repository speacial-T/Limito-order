package com.limito.order.order.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.request.CreateResellOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForCompanyResponseV1;
import com.limito.order.order.service.OrderServiceV1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderControllerV1 {
	private final OrderServiceV1 orderService;

	@PostMapping("/limited")
	public ResponseEntity<CreateLimitedOrderResponseV1> createLimitedOrder(
		@Valid @RequestBody CreateLimitedOrderRequestV1 createLimitedOrderRequest) {
		// Todo. userId 헤더에서 빼오기, 권한검증
		Long userId = 1111L;
		CreateLimitedOrderResponseV1 result = orderService.createLimitedOrder(userId, createLimitedOrderRequest);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/resell")
	public ResponseEntity<CreateResellOrderResponseV1> createResellOrder(
		@Valid @RequestBody CreateResellOrderRequestV1 createResellOrderRequest) {
		// Todo. userId 헤더에서 빼오기, 권한검증
		Long userId = 1111L;
		CreateResellOrderResponseV1 result = orderService.createResellOrder(userId, createResellOrderRequest);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/company")
	public ResponseEntity<Slice<GetOrdersForCompanyResponseV1>> getOrdersForCompany(
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader("X-User-Role") String userRole,
		Pageable pageable
	) {
		Slice<GetOrdersForCompanyResponseV1> response = orderService.getOrdersForCompany(userId, userRole, pageable);

		return ResponseEntity.ok(response);
	}
}
