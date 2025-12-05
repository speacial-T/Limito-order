package com.limito.order.order.domain.dto.response;

import java.util.UUID;

import com.limito.order.common.ProductType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateLimitedOrderItemResponseV1 {
	private UUID orderItemId;

	private UUID optionId;
	private UUID productItemId;

	private ProductType productType;
	private String productName;
	private String brandName;
	private Long sellerId;

	private String productColor;
	private String productSize;

	private int productPrice;
	private int productAmount;

	private Long totalProductPrice;
}
