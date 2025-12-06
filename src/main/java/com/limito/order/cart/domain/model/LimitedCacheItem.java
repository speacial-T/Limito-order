package com.limito.order.cart.domain.model;

import java.util.UUID;

import com.limito.order.common.ProductType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LimitedCacheItem {
	private UUID optionId;
	private UUID productItemId;
	private String productName;
	private String productColor;
	private String productSize;
	private int productPrice;
	private String brandName;
	private String thumbnailUrl;
	private Long sellerId;
	private String productStatus;
	private ProductType productType;
	private int productAmount;
}
