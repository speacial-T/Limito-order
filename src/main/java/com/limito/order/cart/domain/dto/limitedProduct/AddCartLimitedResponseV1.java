package com.limito.order.cart.domain.dto.limitedProduct;

import java.util.UUID;

import com.limito.order.common.ProductType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCartLimitedResponseV1 {
	private UUID optionId;
	private UUID stockId;
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

	public static AddCartLimitedResponseV1 toResDto(AddCartLimitedRequestV1 req) {
		AddCartLimitedResponseV1 res = new AddCartLimitedResponseV1();
		res.setOptionId(req.getOptionId());
		res.setStockId(req.getStockId());
		res.setProductName(req.getProductName());
		res.setProductColor(req.getProductColor());
		res.setProductSize(req.getProductSize());
		res.setProductPrice(req.getProductPrice());
		res.setBrandName(req.getBrandName());
		res.setThumbnailUrl(req.getThumbnailUrl());
		res.setSellerId(req.getSellerId());
		res.setProductStatus(req.getProductStatus());
		res.setProductType(req.getProductType());
		res.setProductAmount(req.getProductAmount());
		return res;
	}
}
