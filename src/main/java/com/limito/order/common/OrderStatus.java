package com.limito.order.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	ORDER_FINISH("주문완료"), ORDER_CANCEL("주문취소"), REFUND("환불"), EXCHANGE("교환"), FAILED_PAYMENTS("결제실패"),
	DELIVERY_PENDING("배송 대기중"), DELIVERY("배송중"), DELIVERY_FINISH("배송완료");

	private final String status;
}
