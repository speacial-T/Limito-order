package com.limito.order.cart.domain.dto.resellProduct;

import com.limito.order.common.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CartAddResellProductReqDtoV1 {
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
//    private int productAmount;
}
