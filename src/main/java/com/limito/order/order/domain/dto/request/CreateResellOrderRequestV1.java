package com.limito.order.order.domain.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResellOrderRequestV1 {
	@NotBlank(message = "수령인은 필수입니다.")
	private String receiverName;

	@NotBlank(message = "전화번호는 필수입니다.")
	@Pattern(
		regexp = "^010-\\d{4}-\\d{4}$",
		message = "전화번호 형식은 010-1234-5678 형태여야 합니다."
	)
	private String phoneNumber;

	@NotBlank(message = "배송지 주소는 필수입니다.")
	private String deliveryAddress;

	@NotNull(message = "전체가격은 필수입니다.")
	@Positive(message = "가격은 양수이어야 합니다.")
	private Long totalPrice;

	@Size(min = 1, message = "주문 상품은 최소 1개 이상이어야 합니다.")
	private List<CreateResellOrderItemRequestV1> items;
}
