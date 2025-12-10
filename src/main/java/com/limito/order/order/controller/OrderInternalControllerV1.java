package com.limito.order.order.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.order.order.service.OrderInternalServiceV1;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/v1/orders")
@RequiredArgsConstructor
public class OrderInternalControllerV1 {
	private final OrderInternalServiceV1 orderInternalService;

	@PostMapping("/success-payments/limited/{orderId}")
	public ResponseEntity<Void> limitedOrderUpdate(@PathVariable UUID orderId) {
		orderInternalService.limitedOrderUpdate(orderId);
		return ResponseEntity.ok().body(null);
	}

	@PostMapping("/success-payments/resell/{orderId}")
	public ResponseEntity<Void> resellOrderFinish(@PathVariable UUID orderId) {
		orderInternalService.resellOrderFinish(orderId);
		return ResponseEntity.ok().body(null);
	}
}
