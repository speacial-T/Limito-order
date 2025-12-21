package com.limito.order.cart.domain.dto.feignclient.limited;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetInCartProductInfoResponseV1 {
	private String productName;
	private String productColor;
	private String productSize;
	private int productPrice;
	private String brandName;
	private String thumbnailUrl;
	private Long sellerId;
	private String productStatus;
	private Boolean isSoldOut;
}
