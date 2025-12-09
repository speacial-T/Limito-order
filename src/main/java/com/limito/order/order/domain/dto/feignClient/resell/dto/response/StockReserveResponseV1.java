package com.limito.order.order.domain.dto.feignClient.resell.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
public class StockReserveResponseV1 implements InternalResponse {

	private String errorCode;

	private String message;

	@Setter
	private List<UUID> stockIds;

}