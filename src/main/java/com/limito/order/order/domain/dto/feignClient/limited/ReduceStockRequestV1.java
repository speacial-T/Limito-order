package com.limito.order.order.domain.dto.feignClient.limited;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReduceStockRequestV1 {
	@NotNull(message = "상품 목록은 null일 수 없습니다.")
	@Size(min = 1, message = "요청값에 최소 한 개의 상품이 있어야 합니다.")
	@Valid
	List<ReduceStockProductRequestV1> products;
}
