package com.limito.order.order.domain.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetOrderForUserResponseV1 {

	OrderInfoResponse order;
	List<OrderItemInfoResponse> orderItems;
}
