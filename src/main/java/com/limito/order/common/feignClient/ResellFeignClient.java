package com.limito.order.common.feignClient;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.limito.order.order.domain.dto.feignClient.resell.dto.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignClient.resell.dto.response.InternalResponse;

import jakarta.validation.Valid;

@FeignClient(name = "resell-product-service", url = "${feign.resell-product-service.url}")
public interface ResellFeignClient {
	// 임시 재고 예약
	@PostMapping("/internal/v1/resell-products/stock/reserve")
	ResponseEntity<InternalResponse> reserveStock(@RequestBody List<UUID> stockIds);

	// 재고 차감
	@PostMapping("/internal/v1/resell-products/stock/reduce")
	ResponseEntity<InternalResponse> reduceStock(@Valid @RequestBody List<StockReduceRequest> request);
}