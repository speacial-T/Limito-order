package com.limito.order.order.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.order.order.service.OrderInternalServiceV1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/v1/orders")
@RequiredArgsConstructor
public class OrderInternalControllerV1 {
	private final OrderInternalServiceV1 orderInternalService;

	// 한정판매 주문 완료
	@PostMapping("/success-payments/limited/{orderId}")
	public ResponseEntity<Void> limitedOrderUpdate(@PathVariable UUID orderId) {
		orderInternalService.limitedOrderUpdate(orderId);
		return ResponseEntity.ok().body(null);
	}

	// 리셀 주문 완료
	@PostMapping("/success-payments/resell/{orderId}")
	public ResponseEntity<Void> resellOrderFinish(@PathVariable UUID orderId) {
		orderInternalService.resellOrderFinish(orderId);
		return ResponseEntity.ok().body(null);
	}

	// 한정판매 결제 실패(취소) 요청
	@DeleteMapping("fail-payments/limited/{orderId}")
	public ResponseEntity<Void> limitedOrderFail(@PathVariable UUID orderId) {
		orderInternalService.limitedOrderFail(orderId);
		return ResponseEntity.ok().body(null);
	}

	// 리셀 결제 실패(취소) 요청
	@DeleteMapping("fail-payments/resell/{orderId}")
	public ResponseEntity<Void> resellOrderFail(@PathVariable UUID orderId) {
		orderInternalService.resellOrderFail(orderId);
		return ResponseEntity.ok().body(null);
	}

	// 한정판매 주문 취소
	@PatchMapping("/{orderId}/cancel/limited")
	public ResponseEntity<Void> limitedOrderCancel(
		@Valid @NotNull(message = "주문 아이디는 필수입니다.") @PathVariable UUID orderId) {
		orderInternalService.limitedOrderCancel(orderId);
		return ResponseEntity.ok().body(null);
	}

	// 리셀 주문 취소
	@PatchMapping("/{orderId}/cancel/resell")
	public ResponseEntity<Void> resellOrderCancel(
		@Valid @NotNull(message = "주문 아이디는 필수입니다.") @PathVariable UUID orderId) {
		orderInternalService.resellOrderCancel(orderId);
		return ResponseEntity.ok().body(null);
	}
}
