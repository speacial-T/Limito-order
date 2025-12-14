package com.limito.order.order.domain.dto.feignclient.resell.response;

import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public class StockReduceResponseV1 implements InternalResponse {

	private String errorCode;

	private String message;

	private List<UUID> stockIds;
}
