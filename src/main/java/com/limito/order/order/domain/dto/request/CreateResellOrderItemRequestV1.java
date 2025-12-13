package com.limito.order.order.domain.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateResellOrderItemRequestV1 {
	@NotNull(message = "옵션 아이디는 필수입니다.")
	private UUID optionId;

	@NotNull(message = "재고 아이디는 필수입니다.")
	private UUID stockId;

	@NotNull(message = "상품 아이디는 필수입니다.")
	private UUID productId;

	private int productAmount = 1;
}
