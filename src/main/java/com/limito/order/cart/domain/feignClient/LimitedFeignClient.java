package com.limito.order.cart.domain.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.limito.order.cart.domain.dto.limitedProduct.GetPurchaseAmountLimitRequestV1;
import com.limito.order.cart.domain.dto.limitedProduct.GetPurchaseAmountLimitResponseV1;
import com.limito.order.cart.domain.dto.limitedProduct.ReserveStockRequestV1;

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
