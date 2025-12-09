package com.limito.order.order.domain.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateLimitedOrderRequestV1 {
	@NotNull(message = "전체가격은 필수입니다.")
	@Positive(message = "가격은 양수이어야 합니다.")
	private Long totalPrice;

	@NotNull(message = "주문 아이템은 필수입니다.")
	@Size(min = 1, message = "주문 상품은 최소 1개 이상이어야 합니다.")
	private List<CreateLimitedOrderItemRequestV1> items;
}
