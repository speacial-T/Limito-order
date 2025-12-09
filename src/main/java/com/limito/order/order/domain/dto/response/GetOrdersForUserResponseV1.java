package com.limito.order.order.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetOrdersForUserResponseV1 {

	private UUID orderId;
	private String itemSummary;
	private String productType;
	private Long totalPrice;
	private LocalDateTime successAt;
}
