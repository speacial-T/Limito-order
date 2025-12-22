package com.limito.order.common.feignclient;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.limito.order.cart.domain.dto.feignclient.resell.OptionInfosGetResponseV1;
import com.limito.order.order.domain.dto.feignclient.resell.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignclient.resell.request.StockRollbackRequest;
import com.limito.order.order.domain.dto.feignclient.resell.response.ProductInfosGetResponseV1;

import jakarta.validation.Valid;

@FeignClient(name = "resell-product-service")
public interface ResellFeignClient {
	// 장바구니 추가 상품 정뵤
	@GetMapping("/internal/v1/resell-products/optionInfo")
	ResponseEntity<List<OptionInfosGetResponseV1>> getOptionInfos(@Valid @RequestParam List<UUID> optionIds);

	// 임시 재고 예약
	@PostMapping("/internal/v1/resell-products/stock/reserve")
	ResponseEntity<Void> reserveStock(@RequestBody List<UUID> stockIds);

	// 재고 차감
	@PostMapping("/internal/v1/resell-products/stock/reduce")
	ResponseEntity<Void> reduceStock(@Valid @RequestBody List<StockReduceRequest> request);

	// 임시 재고 예약 취소
	@PostMapping("/internal/v1/resell-products/stock/cancel")
	ResponseEntity<Void> cancelStock(@RequestBody List<UUID> stockIds);

	// 재고 복원
	@PostMapping("/internal/v1/resell-products/stock/rollback")
	ResponseEntity<Void> rollbackStock(@Valid @RequestBody List<StockRollbackRequest> request);

	// 주문 상품 정보 요청
	@GetMapping("/internal/v1/resell-products/stockInfo")
	ResponseEntity<List<ProductInfosGetResponseV1>> getStockInfos(@Valid @RequestParam List<UUID> stockIds);
}
