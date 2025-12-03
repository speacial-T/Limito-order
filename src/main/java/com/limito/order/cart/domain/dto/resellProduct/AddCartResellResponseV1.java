package com.limito.order.cart.domain.dto.resellProduct;

import java.util.UUID;

import com.limito.order.common.ProductType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCartResellResponseV1 {
	private UUID optionId;
	private UUID stockId;
	private String productName;
	private String productColor;
	private String productSize;
	private int productPrice;
	private String brandName;
	private String thumbnailUrl;
	private Long sellerId;
	private ProductType productType;
	private int productAmount = 1;

	public static AddCartResellResponseV1 toResDto(AddCartResellRequestV1 req) {
		AddCartResellResponseV1 res = new AddCartResellResponseV1();
		res.setOptionId(req.getOptionId());
		res.setStockId(req.getStockId());
		res.setProductName(req.getProductName());
		res.setProductColor(req.getProductColor());
		res.setProductSize(req.getProductSize());
		res.setProductPrice(req.getProductPrice());
		res.setBrandName(req.getBrandName());
		res.setThumbnailUrl(req.getThumbnailUrl());
		res.setSellerId(req.getSellerId());
		res.setProductType(req.getProductType());
		res.setProductAmount(req.getProductAmount());
		return res;
	}
}
