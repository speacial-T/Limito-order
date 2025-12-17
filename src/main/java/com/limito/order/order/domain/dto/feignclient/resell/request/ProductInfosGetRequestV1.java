package com.limito.order.order.domain.dto.feignclient.resell.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductInfosGetRequestV1 {
	@NotNull
	private UUID productId;

	@NotNull
	private UUID optionId;

	@NotNull
	private UUID stockId;
}
