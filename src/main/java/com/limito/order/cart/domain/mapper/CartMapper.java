package com.limito.order.cart.domain.mapper;

import com.limito.order.cart.domain.dto.limitedProduct.AddCartLimitedRequestV1;
import com.limito.order.cart.domain.dto.limitedProduct.AddCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.resellProduct.AddCartResellRequestV1;
import com.limito.order.cart.domain.dto.resellProduct.AddCartResellResponseV1;
import com.limito.order.cart.domain.model.LimitedCacheItem;
import com.limito.order.cart.domain.model.ResellCacheItem;

public class CartMapper {

	public static LimitedCacheItem toDomain(AddCartLimitedRequestV1 req) {
		return LimitedCacheItem.builder()
			.optionId(req.getOptionId())
			.productItemId(req.getProductItemId())
			.productName(req.getProductName())
			.productColor(req.getProductColor())
			.productSize(req.getProductSize())
			.productPrice(req.getProductPrice())
			.brandName(req.getBrandName())
			.thumbnailUrl(req.getThumbnailUrl())
			.sellerId(req.getSellerId())
			.productStatus(req.getProductStatus())
			.productType(req.getProductType())
			.productAmount(req.getProductAmount())
			.build();
	}

	public static AddCartLimitedResponseV1 toResponse(LimitedCacheItem domain) {
		return AddCartLimitedResponseV1.builder()
			.optionId(domain.getOptionId())
			.productItemId(domain.getProductItemId())
			.productName(domain.getProductName())
			.productColor(domain.getProductColor())
			.productSize(domain.getProductSize())
			.productPrice(domain.getProductPrice())
			.brandName(domain.getBrandName())
			.thumbnailUrl(domain.getThumbnailUrl())
			.sellerId(domain.getSellerId())
			.productStatus(domain.getProductStatus())
			.productType(domain.getProductType())
			.productAmount(domain.getProductAmount())
			.build();
	}

	public static ResellCacheItem toDomain(AddCartResellRequestV1 req) {
		return ResellCacheItem.builder()
			.optionId(req.getOptionId())
			.stockId(req.getStockId())
			.productId(req.getProductId())
			.productName(req.getProductName())
			.productColor(req.getProductColor())
			.productSize(req.getProductSize())
			.productPrice(req.getProductPrice())
			.brandName(req.getBrandName())
			.thumbnailUrl(req.getThumbnailUrl())
			.sellerId(req.getSellerId())
			.productType(req.getProductType())
			.build();
	}

	public static AddCartResellResponseV1 toResponse(ResellCacheItem domain) {
		return AddCartResellResponseV1.builder()
			.optionId(domain.getOptionId())
			.stockId(domain.getStockId())
			.productId(domain.getProductId())
			.productName(domain.getProductName())
			.productColor(domain.getProductColor())
			.productSize(domain.getProductSize())
			.productPrice(domain.getProductPrice())
			.brandName(domain.getBrandName())
			.thumbnailUrl(domain.getThumbnailUrl())
			.sellerId(domain.getSellerId())
			.productType(domain.getProductType())
			.build();
	}
}
