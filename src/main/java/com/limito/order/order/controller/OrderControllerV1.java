package com.limito.order.order.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.order.order.domain.dto.request.AddOrdererRequestV1;
import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.request.CreateResellOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderResponseV1;
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

	@PostMapping("/resell/order-sheet")
	public ResponseEntity<CreateResellOrderResponseV1> createResellOrderSheet(
		@Valid @RequestBody CreateResellOrderRequestV1 createResellOrderRequest) {
		// Todo. userId 헤더에서 빼오기, 권한검증
		Long userId = 1111L;
		CreateResellOrderResponseV1 result = orderService.createResellOrderSheet(userId, createResellOrderRequest);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/resell/orderer-data/{orderId}")
	public ResponseEntity<CreateResellOrderResponseV1> addOrderData(@PathVariable UUID orderId,
		@Valid @RequestBody AddOrdererRequestV1 ordererRequest) {
		Long userId = 1111L;
		CreateResellOrderResponseV1 result = orderService.addOrderData(userId, orderId, ordererRequest);
		return ResponseEntity.ok(result);
	}
}
