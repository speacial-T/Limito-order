package com.limito.order.common.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.limito.order.cart.domain.dto.feignclient.GetPurchaseAmountLimitRequestV1;
import com.limito.order.cart.domain.dto.feignclient.GetPurchaseAmountLimitResponseV1;
import com.limito.order.order.domain.dto.feignclient.limited.CancelReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReduceStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.RollbackStockRequestV1;

import jakarta.validation.Valid;

@FeignClient(name = "limited-product-service", url = "${feign.limited-product-service.url}")
public interface LimitedFeignClient {

	@PostMapping("/internal/v1/limited-products/purchase-amount-limit")
	ResponseEntity<GetPurchaseAmountLimitResponseV1> getPurchaseAmountLimits(
		@Valid @RequestBody GetPurchaseAmountLimitRequestV1 request
	);

	@PostMapping("/internal/v1/limited-products/stock/reserve")
	ResponseEntity<Void> reserveStock(@Valid @RequestBody ReserveStockRequestV1 request);

	@PostMapping("/internal/v1/limited-products/stock/reduce")
	ResponseEntity<Void> reduceStock(@Valid @RequestBody ReduceStockRequestV1 request);

	@PostMapping("/stock/reserve/cancel")
	public ResponseEntity<Void> cancelReserveStock(@Valid @RequestBody CancelReserveStockRequestV1 request);

	@PostMapping("/stock/rollback")
	public ResponseEntity<Void> rollbackStock(@Valid @RequestBody RollbackStockRequestV1 request);
}
