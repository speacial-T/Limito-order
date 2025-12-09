package com.limito.order.order.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetOrdersForCompanyResponseV1 {

	private UUID orderId;
	private String itemSummary;
	private Long totalPrice;
	private LocalDateTime successAt;
}
