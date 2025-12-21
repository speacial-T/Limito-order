package com.limito.order.cart.domain.dto.resellproduct;

import java.util.UUID;

import com.limito.order.common.ProductType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddCartResellResponseV1 {
	private UUID optionId;
	private UUID stockId;
	private UUID productId;
	private ProductType productType;
}
