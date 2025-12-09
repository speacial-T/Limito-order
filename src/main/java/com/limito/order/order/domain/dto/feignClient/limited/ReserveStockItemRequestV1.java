package com.limito.order.order.domain.dto.feignClient.limited;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStockItemRequestV1 {
	@NotNull(message = "아이템 id는 null일 수 없습니다.")
	UUID limitedProductItemId;

	@NotNull(message = "수량은 null일 수 없습니다.")
	@Positive(message = "최소 구매 수량은 1개입니다.")
	int amount;

	public static ReserveStockItemRequestV1 of(UUID limitedProductItemId, int amount) {
		return ReserveStockItemRequestV1.builder()
			.limitedProductItemId(limitedProductItemId)
			.amount(amount)
			.build();
	}
}