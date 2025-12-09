package com.limito.order.order.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.order.order.service.OrderServiceV1;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/v1/orders/")
@RequiredArgsConstructor
public class OrderFeignControllerV1 {
	private final OrderServiceV1 orderService;

	@PostMapping("/afterPayments/{orderId}")
	public ResponseEntity<Void> afterPaymentsSuccessed(@PathVariable UUID orderId) {
		orderService.afterPaymentsSuccessed(orderId);
		return ResponseEntity.ok().body(null);
	}
}
