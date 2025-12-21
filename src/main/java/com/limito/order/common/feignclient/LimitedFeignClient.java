package com.limito.order.common.feignclient;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.limito.order.cart.domain.dto.feignclient.limited.GetInCartProductInfoResponseV1;
import com.limito.order.cart.domain.dto.feignclient.limited.GetPurchaseAmountLimitRequestV1;
import com.limito.order.cart.domain.dto.feignclient.limited.GetPurchaseAmountLimitResponseV1;
import com.limito.order.order.domain.dto.feignclient.limited.CancelReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.GetOrderedProductInfoRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.GetOrderedProductInfoResponseV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReduceStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.RollbackStockRequestV1;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@FeignClient(name = "limited-product-service", url = "${feign.limited-product-service.url}")
public interface LimitedFeignClient {

	// 최대 구매 가능 수량 확인
	@PostMapping("/internal/v1/limited-products/purchase-amount-limit")
	ResponseEntity<GetPurchaseAmountLimitResponseV1> getPurchaseAmountLimits(
		@Valid @RequestBody GetPurchaseAmountLimitRequestV1 request
	);

	// 장바구니 추가 상품 정보
	@GetMapping("/in-cart-product/{limitedProductItemId}")
	public ResponseEntity<GetInCartProductInfoResponseV1> getInCartProductInfo(
		@NotNull(message = "아이템 id는 null일 수 없습니다.")
		@PathVariable UUID limitedProductItemId
	);

	// 임시 재고 예약
	@PostMapping("/internal/v1/limited-products/stock/reserve")
	ResponseEntity<Void> reserveStock(@Valid @RequestBody ReserveStockRequestV1 request);

	// 재고 차감
	@PostMapping("/internal/v1/limited-products/stock/reduce")
	ResponseEntity<Void> reduceStock(@Valid @RequestBody ReduceStockRequestV1 request);

	// 임시 재고 예약 취소
	@PostMapping("/internal/v1/limited-products/stock/reserve/cancel")
	public ResponseEntity<Void> cancelReserveStock(@Valid @RequestBody CancelReserveStockRequestV1 request);

	// 재고 복원
	@PostMapping("/internal/v1/limited-products/stock/rollback")
	public ResponseEntity<Void> rollbackStock(@Valid @RequestBody RollbackStockRequestV1 request);

	// 주문 상품 정보 조회
	@PostMapping("/internal/v1/limited-products/ordered-products")
	public ResponseEntity<GetOrderedProductInfoResponseV1> getOrderedProductInfo(
		@Valid @RequestBody GetOrderedProductInfoRequestV1 request
	);
}
