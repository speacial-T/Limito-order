package com.limito.order.order.domain.dto.feignclient.resell.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductInfosGetResponseV1 {
	private UUID productId;
	private UUID optionId;
	private UUID stockId;
	private String productType;
	private String productName;
	private String brandName;
	private Long sellerId;
	private String productColor;
	private String productSize;
	private int productPrice;
}