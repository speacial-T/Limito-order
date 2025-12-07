package com.limito.order.cart.domain.dto.feignClient;

import java.util.UUID;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetPurchaseAmountLimitRequestV1 {
	@Size(min = 1, message = "요청값에 최소 한 개의 아이템이 있어야 합니다.")
	UUID itemIdList;
}
