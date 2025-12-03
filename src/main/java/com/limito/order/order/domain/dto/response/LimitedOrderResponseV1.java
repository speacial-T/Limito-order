package com.limito.order.order.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.limito.order.common.OrderStatus;
import com.limito.order.order.domain.dto.request.LimitedOrderItemRequestV1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitedOrderResponseV1 {
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

	private List<LimitedOrderItemRequestV1> items;
}
