package com.limito.order.order.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderInfoResponse {

	private UUID orderId;
	private Long userId;
	private String receiverName;
	private String phoneNumber;
	private String deliveryAddress;
	private Long totalPrice;
	private String orderStatus;
	private String cancelReason;
	private LocalDateTime successedAt;
	private String itemSummary;
}
