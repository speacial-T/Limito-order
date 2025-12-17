package com.limito.order.order.domain.dto.feignclient.limited;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductOptionItemRequest {
	@NotNull(message = "상품 id는 null일 수 없습니다.")
	UUID limitedProductId;

	@NotNull(message = "옵션 id는 null일 수 없습니다.")
	UUID limitedProductOptionId;

	@NotNull(message = "아이템 id는 null일 수 없습니다.")
	UUID limitedProductItemId;

}
