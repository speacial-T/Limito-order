package com.limito.order.cart.domain.dto.resellProduct;

import com.limito.order.common.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddCartResellRequestV1 {
    @NotNull(message = "옵션 아이디는 필수입니다.")
    private UUID optionId;

    @NotNull(message = "재고 아이디는 필수입니다.")
    private UUID stockId;

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String productName;

    @NotBlank(message = "상품 색상은 필수입니다.")
    private String productColor;

    @NotBlank(message = "상품 사이즈는 필수입니다.")
    private String productSize;

    @NotNull(message = "상품 가격은 필수입니다.")
    private int productPrice;

    @NotBlank(message = "브랜드먕은 필수입니다.")
    private String brandName;

    @NotBlank(message = "대표 이미지 url은 필수입니다.")
    private String thumbnailUrl;

    @NotNull(message = "판매자 아이디는 필수입니다.")
    private Long sellerId;

    @NotNull(message = "상품 타입은 필수입니다.")
    private ProductType productType;

    private int productAmount = 1;
}
