package com.limito.order.cart.domain.mapper;

import com.limito.order.cart.domain.dto.feignclient.limited.GetInCartProductInfoResponseV1;
import com.limito.order.cart.domain.dto.feignclient.resell.OptionInfosGetResponseV1;
import com.limito.order.cart.domain.dto.limitedproduct.AddCartLimitedRequestV1;
import com.limito.order.cart.domain.dto.limitedproduct.AddCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.limitedproduct.GetCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.resellproduct.AddCartResellRequestV1;
import com.limito.order.cart.domain.dto.resellproduct.AddCartResellResponseV1;
import com.limito.order.cart.domain.dto.resellproduct.GetCartResellResponseV1;
import com.limito.order.cart.domain.model.LimitedCacheItem;
import com.limito.order.cart.domain.model.ResellCacheItem;

public class CartMapper {

	public static LimitedCacheItem toDomain(AddCartLimitedRequestV1 req, GetInCartProductInfoResponseV1 productInfo) {
		return LimitedCacheItem.builder()
			.optionId(req.getOptionId())
			.productItemId(req.getProductItemId())
			.productName(productInfo.getProductName())
			.productColor(productInfo.getProductColor())
			.productSize(productInfo.getProductSize())
			.productPrice(productInfo.getProductPrice())
			.brandName(productInfo.getBrandName())
			.thumbnailUrl(productInfo.getThumbnailUrl())
			.sellerId(productInfo.getSellerId())
			.productStatus(productInfo.getProductStatus())
			.isSoldOut(productInfo.getIsSoldOut())
			.productType(req.getProductType())
			.productAmount(req.getProductAmount())
			.build();
	}

	public static AddCartLimitedResponseV1 toAddResponse(LimitedCacheItem domain) {
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

	public static GetCartLimitedResponseV1 toGetResponse(LimitedCacheItem domain) {
		return GetCartLimitedResponseV1.builder()
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
			.productType(req.getProductType())
			.build();
	}

	public static AddCartResellResponseV1 toAddResponse(ResellCacheItem domain) {
		return com.limito.order.cart.domain.dto.resellproduct.AddCartResellResponseV1.builder()
			.optionId(domain.getOptionId())
			.stockId(domain.getStockId())
			.productId(domain.getProductId())
			.productType(domain.getProductType())
			.build();
	}

	public static GetCartResellResponseV1 toGetResponse(ResellCacheItem domain, OptionInfosGetResponseV1 optionInfo) {
		return GetCartResellResponseV1.builder()
			.optionId(domain.getOptionId())
			.stockId(domain.getStockId())
			.productId(domain.getProductId())
			.productName(optionInfo.getProductName())
			.productColor(optionInfo.getProductColor())
			.productSize(optionInfo.getProductSize())
			.productPrice(optionInfo.getProductPrice())
			.brandName(optionInfo.getBrandName())
			.thumbnailUrl(optionInfo.getThumbnailUrl())
			.sellerId(optionInfo.getSellerId())
			.productType(domain.getProductType())
			.build();
	}
}
