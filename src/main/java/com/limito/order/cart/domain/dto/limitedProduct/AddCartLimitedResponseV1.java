package com.limito.order.cart.domain.dto.limitedProduct;

import com.limito.order.common.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddCartLimitedResponseV1 {
    private UUID optionId;
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
