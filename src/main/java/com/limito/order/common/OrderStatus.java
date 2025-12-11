package com.limito.order.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	ORDER_PENDING("주문서 생성"), ORDER_FINISH("주문완료"),
	ORDER_FAIL("주문실패"), ORDER_CANCEL("주문취소");

	private final String description;
}
