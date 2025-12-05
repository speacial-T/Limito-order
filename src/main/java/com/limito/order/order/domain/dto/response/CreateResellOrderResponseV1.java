package com.limito.order.order.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.limito.order.common.OrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateResellOrderResponseV1 {
	private UUID orderId;

	private Long userId;
	private String receiverName;
	private String phoneNumber;
	private String deliveryAddress;

	private Long totalPrice;

	private OrderStatus orderStatus;
	private LocalDateTime successedAt;
	private String itemSummary;
	// private String cancelReason;

	private List<CreateResellOrderItemResponseV1> items;
}
