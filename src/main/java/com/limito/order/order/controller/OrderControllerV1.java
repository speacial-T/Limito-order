package com.limito.order.order.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.order.order.domain.dto.request.AddOrdererRequestV1;
import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.request.CreateResellOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderResponseV1;
import com.limito.order.order.domain.dto.response.GetOrderForCompanyResponseV1;
import com.limito.order.order.domain.dto.response.GetOrderForUserResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForCompanyResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForUserResponseV1;
import com.limito.order.order.service.OrderServiceV1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderControllerV1 {
	private final OrderServiceV1 orderService;

	// 한정판매 주문서 생성
	@PostMapping("/limited/order-sheet")
	public ResponseEntity<CreateLimitedOrderResponseV1> createLimitedOrderSheet(
		// @RequestHeader("X-User-Id") Long userId,
		@Valid @RequestBody CreateLimitedOrderRequestV1 createLimitedOrderRequest) {
		// Todo. userId 헤더에서 빼오기, 권한검증
		Long userId = 1111L;
		CreateLimitedOrderResponseV1 result = orderService.createLimitedOrderSheet(userId, createLimitedOrderRequest);
		return ResponseEntity.ok(result);
	}

	// 한정판매 주문자 정보 추가
	@PostMapping("/limited/orderer-data/{orderId}")
	public ResponseEntity<CreateLimitedOrderResponseV1> addLimitedOrdererData(@PathVariable UUID orderId,
		@Valid @RequestBody AddOrdererRequestV1 ordererRequest) {
		Long userId = 1111L;
		CreateLimitedOrderResponseV1 result = orderService.addLimitedOrdererData(userId, orderId, ordererRequest);
		return ResponseEntity.ok(result);
	}

	// 리셀 주문서 생성
	@PostMapping("/resell/order-sheet")
	public ResponseEntity<CreateResellOrderResponseV1> createResellOrderSheet(
		// @RequestHeader("X-User-Id") Long userId,
		@Valid @RequestBody CreateResellOrderRequestV1 createResellOrderRequest) {
		// Todo. userId 헤더에서 빼오기, 권한검증
		Long userId = 1111L;
		CreateResellOrderResponseV1 result = orderService.createResellOrderSheet(userId, createResellOrderRequest);
		return ResponseEntity.ok(result);
	}

	// 리셀 주문자 정보 추가
	@PostMapping("/resell/orderer-data/{orderId}")
	public ResponseEntity<CreateResellOrderResponseV1> addResellOrdererData(@PathVariable UUID orderId,
		@Valid @RequestBody AddOrdererRequestV1 ordererRequest) {
		Long userId = 1111L;
		CreateResellOrderResponseV1 result = orderService.addResellOrdererData(userId, orderId, ordererRequest);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/user")
	public ResponseEntity<Slice<GetOrdersForUserResponseV1>> getOrdersForUser(
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader("X-User-Role") String userRole,
		Pageable pageable
	) {
		Slice<GetOrdersForUserResponseV1> response = orderService.getOrdersForUser(userId, userRole, pageable);

		return ResponseEntity.ok(response);
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

	@GetMapping("/user/{orderId}")
	public ResponseEntity<GetOrderForUserResponseV1> getOrderForUser(
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader("X-User-Role") String userRole,
		@PathVariable UUID orderId
	) {
		GetOrderForUserResponseV1 response = orderService.getOrderForUser(userId, userRole, orderId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/company/{orderId}")
	public ResponseEntity<GetOrderForCompanyResponseV1> getOrderForCompany(
		@RequestHeader("X-User-Id") Long userId,
		@RequestHeader("X-User-Role") String userRole,
		@PathVariable UUID orderId
	) {
		GetOrderForCompanyResponseV1 response = orderService.getOrderForCompany(userId, userRole, orderId);

		return ResponseEntity.ok(response);
	}
}
