package com.limito.order.cart.domain.model;

import java.util.UUID;

import com.limito.order.common.ProductType;

public class ResellCacheItem {
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
