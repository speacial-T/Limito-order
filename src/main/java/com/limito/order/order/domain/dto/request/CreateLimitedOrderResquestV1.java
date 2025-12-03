package com.limito.order.order.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateLimitedOrderResquestV1 {
    @NotNull(message = "주문자 아이디는 필수입니다.")
    private Long userId;

    @NotBlank(message = "수령인은 필수입니다.")
    private String receiverName;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    @NotBlank(message = "배송지 주소는 필수입니다.")
    private String deliveryAddress;

    @NotNull(message = "전체가격은 필수입니다.")
    private Long totalPrice;

    @Size(min = 1, message = "주문 상품은 최소 1개 이상이어야 합니다.")
    private List<LimitedOrderItemRequestV1> items;
}
