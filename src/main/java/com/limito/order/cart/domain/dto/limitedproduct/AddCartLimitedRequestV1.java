package com.limito.order.cart.domain.dto.limitedproduct;

import java.util.UUID;

import com.limito.order.common.ProductType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCartLimitedRequestV1 {
	@NotNull(message = "옵션 아이디는 필수입니다.")
	private UUID optionId;

	@NotNull(message = "판매 아이템 아이디는 필수입니다.")
	private UUID productItemId;

	@NotNull(message = "상품 타입은 필수입니다.")
	private ProductType productType;

	@Positive(message = "상품 수량은 1개 이상이어야 합니다.")
	private int productAmount;
}
