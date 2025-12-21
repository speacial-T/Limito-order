package com.limito.order.cart.domain.dto.feignclient.resell;

import com.limito.order.common.ProductType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OptionInfosGetResponseV1 {
	private String productName;
	private String productColor;
	private String productSize;
	private int productPrice;
	private String brandName;
	private String thumbnailUrl;
	private Long sellerId;
	private ProductType productType;
}
