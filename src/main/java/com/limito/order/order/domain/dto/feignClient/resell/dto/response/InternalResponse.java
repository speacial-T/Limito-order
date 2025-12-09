package com.limito.order.order.domain.dto.feignClient.resell.dto.response;

import java.util.List;
import java.util.UUID;

public interface InternalResponse {

	public String getErrorCode();

	public String getMessage();

	public List<UUID> getStockIds();
}