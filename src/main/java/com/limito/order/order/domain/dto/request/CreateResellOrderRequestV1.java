package com.limito.order.order.domain.dto.request;

import java.util.List;

import com.limito.order.common.ProductType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateResellOrderRequestV1 {
	@NotNull(message = "주문 상품 타입은 필수입니다.")
	private ProductType orderProductType;

	@NotNull(message = "주문 아이템은 필수입니다.")
	@Size(min = 1, message = "주문 상품은 최소 1개 이상이어야 합니다.")
	private List<CreateResellOrderItemRequestV1> items;
}
