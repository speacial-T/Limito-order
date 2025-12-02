package com.limito.order.cart.domain.dto.limitedProduct;

import com.limito.order.common.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CartAddLimitedProductResDtoV1 {
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
}
