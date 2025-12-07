package com.limito.order.cart.domain.dto.limitedProduct;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class GetPurchaseAmountLimitResponseV1 {
	private List<PurchaseAmountLimit> items;

	@Getter
	@Setter
	public static class PurchaseAmountLimit {
		private UUID limitedProductItemId;
		private int purchaseAmountLimit;
	}
}
