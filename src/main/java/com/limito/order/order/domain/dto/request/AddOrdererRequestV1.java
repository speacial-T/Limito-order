package com.limito.order.order.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class AddOrdererRequestV1 {
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
}
