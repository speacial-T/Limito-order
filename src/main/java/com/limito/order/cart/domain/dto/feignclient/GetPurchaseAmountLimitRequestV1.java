package com.limito.order.cart.domain.dto.feignclient;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPurchaseAmountLimitRequestV1 {
	@Size(min = 1, message = "요청값에 최소 한 개의 아이템이 있어야 합니다.")
	List<UUID> itemIdList;

	public static GetPurchaseAmountLimitRequestV1 create(List<UUID> itemIdList) {
		return new GetPurchaseAmountLimitRequestV1(itemIdList);
	}
}
