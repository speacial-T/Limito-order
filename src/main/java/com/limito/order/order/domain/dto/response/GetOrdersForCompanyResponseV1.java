package com.limito.order.order.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record GetOrdersForCompanyResponseV1(
	UUID orderId,
	String itemSummary,
	Long totalPrice,
	LocalDateTime successAt
) {
}
