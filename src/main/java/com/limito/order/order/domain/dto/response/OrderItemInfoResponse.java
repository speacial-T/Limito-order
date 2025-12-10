package com.limito.order.order.domain.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderItemInfoResponse {

	private UUID id;
	private UUID optionId;
	private UUID productItemId;
	private UUID stockId;
	private UUID productId;
	private String productType;
	private String productName;
	private String brandName;
	private Long sellerId;
	private String productColor;
	private String productSize;
	private int productPrice;
	private int productAmount;
	private Long totalProductPrice;
}
