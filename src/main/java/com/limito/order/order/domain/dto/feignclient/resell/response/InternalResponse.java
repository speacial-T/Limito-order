package com.limito.order.order.domain.dto.feignclient.resell.response;

import java.util.List;
import java.util.UUID;

public interface InternalResponse {

	public String getErrorCode();

	public String getMessage();

	public List<UUID> getStockIds();
}
