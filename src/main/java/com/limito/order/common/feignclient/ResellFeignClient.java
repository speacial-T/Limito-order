package com.limito.order.common.feignclient;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.limito.order.order.domain.dto.feignclient.resell.dto.request.ProductInfosGetRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.dto.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignclient.resell.dto.request.StockRollbackRequest;
import com.limito.order.order.domain.dto.feignclient.resell.dto.response.ProductInfosGetResponseV1;

import jakarta.validation.Valid;

@FeignClient(name = "resell-product-service", url = "${feign.resell-product-service.url}")
public interface ResellFeignClient {
	// 임시 재고 예약
	@PostMapping("/internal/v1/resell-products/stock/reserve")
	ResponseEntity<Void> reserveStock(@RequestBody List<UUID> stockIds);

	// 재고 차감
	@PostMapping("/internal/v1/resell-products/stock/reduce")
	ResponseEntity<Void> reduceStock(@Valid @RequestBody List<StockReduceRequest> request);

	// 임시 재고 예약 취소
	@PostMapping("/internal/v1/resell-products/stock/cancel")
	public ResponseEntity<Void> cancelStock(@RequestBody List<UUID> stockIds);

	// 재고 복원
	@PostMapping("/internal/v1/resell-products/stock/rollback")
	public ResponseEntity<Void> rollbackStock(@Valid @RequestBody List<StockRollbackRequest> request);

	// 주문 상품 정보 요청
	@GetMapping("/productInfo")
	public ResponseEntity<List<ProductInfosGetResponseV1>> getProductInfos(
		@RequestBody List<ProductInfosGetRequestV1> request
	);
}
