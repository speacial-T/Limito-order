package com.limito.order.order.domain.dto.feignclient.limited;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemAmountRequest {
	@NotNull(message = "아이템 id는 null일 수 없습니다.")
	UUID limitedProductItemId;

	@NotNull(message = "수량은 null일 수 없습니다.")
	@Positive(message = "수량은 1 이상이어야 합니다.")
	int amount;
}
