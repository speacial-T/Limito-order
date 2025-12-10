package com.limito.order.order.domain.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetOrderForCompanyResponseV1 {

	private OrderInfoResponse order;
	private List<OrderItemInfoResponse> orderItems;
}
