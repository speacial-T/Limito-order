package com.limito.order.order.domain.dto.feignclient.resell.request;

import java.util.UUID;

import com.limito.order.order.domain.model.OrderItem;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockReduceRequest {
	@NotNull
	private UUID productId;

	@NotNull
	private UUID optionId;

	@NotNull
	private UUID stockId;

	public static StockReduceRequest createRequest(OrderItem orderItem) {
		UUID productId = orderItem.getProductId();
		UUID optionId = orderItem.getOptionId();
		UUID stockId = orderItem.getStockId();
		return new StockReduceRequest(productId, optionId, stockId);
	}
}
