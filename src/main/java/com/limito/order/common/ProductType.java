package com.limito.order.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {
	LIMITED("한정판매"), RESELL("리셀");

	private final String type;
}
