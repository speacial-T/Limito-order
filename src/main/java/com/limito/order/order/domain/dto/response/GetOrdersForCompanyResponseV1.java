package com.limito.order.order.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public class GetOrdersForCompanyResponseV1 {
	private UUID orderId;
	private String itemSummary;
	private Long totalPrice;
	private LocalDateTime successAt;
}
