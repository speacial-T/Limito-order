package com.limito.order.cart.domain.model;

import java.util.UUID;

import com.limito.order.common.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResellCacheItem {
	private UUID optionId;
	private UUID productId;
	private ProductType productType;
}
