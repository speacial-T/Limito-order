package com.limito.order.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	ORDER_PENDING("주문서 생성"), ORDER_FINISH("주문완료"), ORDER_CANCEL("주문취소"), REFUND("환불"), EXCHANGE("교환"),
	DELIVERY_PENDING("배송 대기중"), DELIVERY("배송중"), DELIVERY_FINISH("배송완료");

	private final String status;
}
