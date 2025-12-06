package com.limito.order.order.domain.feignClient.resell.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StockReduceRequest {
	@NotNull
	private UUID productId;

	@NotNull
	private UUID optionId;

	@NotNull
	private UUID stockId;
}
