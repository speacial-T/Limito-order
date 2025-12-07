package com.limito.order.common.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.limito.order.cart.domain.dto.feignClient.GetPurchaseAmountLimitRequestV1;
import com.limito.order.cart.domain.dto.feignClient.GetPurchaseAmountLimitResponseV1;
import com.limito.order.cart.domain.dto.feignClient.ReserveStockRequestV1;

import jakarta.validation.Valid;

@FeignClient(name = "limited-product-service", url = "${feign.limited-product-service.url}")
public interface LimitedFeignClient {

	@PostMapping("/internal/v1/limited-products/purchase-amount-limit")
	ResponseEntity<GetPurchaseAmountLimitResponseV1> getPurchaseAmountLimits(
		@Valid @RequestBody GetPurchaseAmountLimitRequestV1 request
	);

	@PostMapping("/internal/v1/limited-products/stock/reserve")
	ResponseEntity<Void> reserveStock(@Valid @RequestBody ReserveStockRequestV1 request);
}
