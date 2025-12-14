package com.limito.order.order.domain.dto.feignclient.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderedUserInfoResponseV1 {

	private Long userId;
	private String receiverName;
	private String phoneNumber;
	private String deliveryAddress;

}