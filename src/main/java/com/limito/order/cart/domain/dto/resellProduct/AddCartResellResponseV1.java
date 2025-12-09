package com.limito.order.cart.domain.dto.resellProduct;

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
	private String productName;
	private String productColor;
	private String productSize;
	private int productPrice;
	private String brandName;
	private String thumbnailUrl;
	private Long sellerId;
	private ProductType productType;
}
