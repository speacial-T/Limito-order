package com.limito.order.order.domain.feignClient.resell;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.limito.order.order.domain.feignClient.resell.dto.request.StockReduceRequest;

import jakarta.validation.Valid;

@FeignClient(name = "resell-product-service")
public interface ResellFeignClient {
	// 임시 재고 예약
	@PostMapping("/internal/v1/resell-products/stock/reserve")
	ResponseEntity<Object> reserveStock(List<UUID> stockIds);

	// 재고 차감
	@PostMapping("/internal/v1/resell-products/stock/reduce")
	ResponseEntity<Object> reduceStock(@Valid @RequestBody List<StockReduceRequest> request);
}