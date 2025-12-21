package com.limito.order.cart.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limito.common.security.audit.UserRole;
import com.limito.common.security.auth.CurrentUser;
import com.limito.common.security.auth.PreAuthorized;
import com.limito.common.security.context.UserContext;
import com.limito.order.cart.domain.dto.limitedproduct.AddCartLimitedRequestV1;
import com.limito.order.cart.domain.dto.limitedproduct.AddCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.limitedproduct.GetCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.resellproduct.AddCartResellRequestV1;
import com.limito.order.cart.domain.dto.resellproduct.AddCartResellResponseV1;
import com.limito.order.cart.domain.dto.resellproduct.GetCartResellResponseV1;
import com.limito.order.cart.service.CartServiceV1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartControllerV1 {
	private final CartServiceV1 cartService;

	// 한정판매 장바구니 추가
	@PreAuthorized({UserRole.USER})
	@PostMapping("/limited")
	public ResponseEntity<AddCartLimitedResponseV1> addLimitedItem(
		@CurrentUser UserContext user,
		@Valid @RequestBody AddCartLimitedRequestV1 addLimitedProductReqDto) {
		AddCartLimitedResponseV1 result = cartService.addLimitedItem(user, addLimitedProductReqDto);
		return ResponseEntity.ok(result);
	}

	// 리셀 장바구니 추가
	@PreAuthorized({UserRole.USER})
	@PostMapping("/resell")
	public ResponseEntity<AddCartResellResponseV1> addResellItem(
		@CurrentUser UserContext user,
		@Valid @RequestBody AddCartResellRequestV1 addResellProductReqDto) {
		AddCartResellResponseV1 result = cartService.addResellItem(user, addResellProductReqDto);
		return ResponseEntity.ok(result);
	}

	// 한정판매 장바구니 조회
	@PreAuthorized({UserRole.USER})
	@GetMapping("/limited")
	public ResponseEntity<List<GetCartLimitedResponseV1>> getLimitedCart(@CurrentUser UserContext user) {
		List<GetCartLimitedResponseV1> results = cartService.getLimitedCart(user);
		return ResponseEntity.ok(results);
	}

	// 리셀 장바구니 조회
	@GetMapping("/resell")
	@PreAuthorized({UserRole.USER})
	public ResponseEntity<List<GetCartResellResponseV1>> getResellCart(@CurrentUser UserContext user) {
		List<GetCartResellResponseV1> results = cartService.getResellCart(user);
		return ResponseEntity.ok(results);
	}

	// 한정판매 장바구니 주문 완료 아이템 삭제
	@DeleteMapping("/limited-ordered")
	public ResponseEntity<Void> deleteLimitedOrderItem(
		@CurrentUser UserContext user,
		@Valid @RequestBody List<UUID> productItemIds) {
		cartService.deleteLimitedOrderItem(user, productItemIds);
		return ResponseEntity.ok(null);
	}

	// 리셀 장바구니 주문 완료 아이템 삭제
	@DeleteMapping("/resell-ordered")
	public ResponseEntity<Void> deleteResellOrderItem(
		@CurrentUser UserContext user,
		@Valid @RequestBody List<UUID> optionIds) {
		cartService.deleteResellOrderItem(user, optionIds);
		return ResponseEntity.ok(null);
	}
}
