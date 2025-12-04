package com.limito.order.order.domain.dto.request;

import java.util.UUID;

import com.limito.order.common.ProductType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLimitedOrderItemRequestV1 {
	@NotNull(message = "옵션 아이디는 필수입니다.")
	private UUID optionId;

	@NotNull(message = "아이템 아이디는 필수입니다.")
	private UUID itemId;

	@NotNull(message = "상품 타입은 필수입니다.")
	private ProductType productType;

	@NotBlank(message = "상품 이름은 필수입니다.")
	private String productName;

	@NotBlank(message = "브랜드명은 필수입니다.")
	private String brandName;

	@NotNull(message = "판매 업체 아이디는 필수입니다.")
	private Long sellerId;

	@NotBlank(message = "상품 색상은 필수입니다.")
	private String productColor;

	@NotBlank(message = "상품 사이즈는 필수입니다.")
	private String productSize;

	@NotBlank(message = "상품 가격은 필수입니다.")
	@Positive
	private int productPrice;

	@Positive(message = "상품 수량은 1개 이상이어야 합니다.")
	private int productAmount;

	@NotNull(message = "상품 별 합산 가격은 필수입니다.")
	private Long totalProductPrice;
}
