package com.limito.order.order.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.common.security.audit.UserRole;
import com.limito.common.security.auth.CurrentUser;
import com.limito.common.security.auth.PreAuthorized;
import com.limito.common.security.context.UserContext;
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
	@PreAuthorized({UserRole.USER})
	@PostMapping("/limited/order-sheet")
	public ResponseEntity<CreateLimitedOrderResponseV1> createLimitedOrderSheet(
		@Valid @RequestBody CreateLimitedOrderRequestV1 createLimitedOrderRequest,
		@CurrentUser UserContext user
	) {
		CreateLimitedOrderResponseV1 result = orderService.createLimitedOrderSheet(user, createLimitedOrderRequest);
		return ResponseEntity.ok(result);
	}

	// 한정판매 주문자 정보 변경
	@PreAuthorized({UserRole.USER})
	@PatchMapping("/limited/orderer-data/{orderId}")
	public ResponseEntity<CreateLimitedOrderResponseV1> addLimitedOrdererData(
		@PathVariable UUID orderId,
		@Valid @RequestBody AddOrdererRequestV1 ordererRequest,
		@CurrentUser UserContext user
	) {
		CreateLimitedOrderResponseV1 result = orderService.addLimitedOrdererData(user, orderId, ordererRequest);
		return ResponseEntity.ok(result);
	}

	// 리셀 주문서 생성
	@PreAuthorized({UserRole.USER})
	@PostMapping("/resell/order-sheet")
	public ResponseEntity<CreateResellOrderResponseV1> createResellOrderSheet(
		@Valid @RequestBody CreateResellOrderRequestV1 createResellOrderRequest,
		@CurrentUser UserContext user
	) {
		CreateResellOrderResponseV1 result = orderService.createResellOrderSheet(user, createResellOrderRequest);
		return ResponseEntity.ok(result);
	}

	// 리셀 주문자 정보 추가
	@PreAuthorized({UserRole.USER})
	@PatchMapping("/resell/orderer-data/{orderId}")
	public ResponseEntity<CreateResellOrderResponseV1> addResellOrdererData(
		@PathVariable UUID orderId,
		@Valid @RequestBody AddOrdererRequestV1 ordererRequest,
		@CurrentUser UserContext user
	) {
		CreateResellOrderResponseV1 result = orderService.addResellOrdererData(user, orderId, ordererRequest);
		return ResponseEntity.ok(result);
	}

	// 주문 목록 조회 - USER 권한
	@PreAuthorized({UserRole.USER})
	@GetMapping("/user")
	public ResponseEntity<Slice<GetOrdersForUserResponseV1>> getOrdersForUser(
		@CurrentUser UserContext user,
		Pageable pageable
	) {
		Slice<GetOrdersForUserResponseV1> response = orderService.getOrdersForUser(user, pageable);

		return ResponseEntity.ok(response);
	}

	// 주문 목록 조회 - COMPANY 권한
	@PreAuthorized({UserRole.COMPANY})
	@GetMapping("/company")
	public ResponseEntity<Slice<GetOrdersForCompanyResponseV1>> getOrdersForCompany(
		@CurrentUser UserContext user,
		Pageable pageable
	) {
		Slice<GetOrdersForCompanyResponseV1> response = orderService.getOrdersForCompany(user, pageable);

		return ResponseEntity.ok(response);
	}

	// 주문 싱세 조회 - USER 권한
	@PreAuthorized({UserRole.USER})
	@GetMapping("/user/{orderId}")
	public ResponseEntity<GetOrderForUserResponseV1> getOrderForUser(
		@CurrentUser UserContext user,
		@PathVariable UUID orderId
	) {
		GetOrderForUserResponseV1 response = orderService.getOrderForUser(user, orderId);

		return ResponseEntity.ok(response);
	}

	// 주문 상세 조회 - COMPANY 권한
	@PreAuthorized({UserRole.COMPANY})
	@GetMapping("/company/{orderId}")
	public ResponseEntity<GetOrderForCompanyResponseV1> getOrderForCompany(
		@CurrentUser UserContext user,
		@PathVariable UUID orderId
	) {
		GetOrderForCompanyResponseV1 response = orderService.getOrderForCompany(user, orderId);

		return ResponseEntity.ok(response);
	}
}
