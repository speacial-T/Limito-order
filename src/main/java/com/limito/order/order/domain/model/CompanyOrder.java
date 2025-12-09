package com.limito.order.order.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
public class CompanyOrder {

	UUID orderId;
	String itemSummary;
	Long totalPrice;
	LocalDateTime successedAt;

	public CompanyOrder(UUID orderId, String itemSummary, Long totalPrice, LocalDateTime successedAt) {
		this.orderId = orderId;
		this.itemSummary = itemSummary;
		this.totalPrice = totalPrice;
		this.successedAt = successedAt;
	}
}
