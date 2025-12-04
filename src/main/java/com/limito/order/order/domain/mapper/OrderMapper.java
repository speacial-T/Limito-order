package com.limito.order.order.domain.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.limito.order.order.domain.dto.request.CreateLimitedOrderResquestV1;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;

@Component
public class OrderMapper {

	public Order toOrderEntity(CreateLimitedOrderResquestV1 req) {
		return Order.builder()
			.userId(req.getUserId())
			.receiverName(req.getReceiverName())
			.phoneNumber(req.getPhoneNumber())
			.deliveryAddress(req.getDeliveryAddress())
			.totalPrice(req.getTotalPrice())
			.orderStatus(com.limito.order.common.OrderStatus.ORDER_FINISH)
			.successedAt(LocalDateTime.now())
			.build();
	}

	public List<OrderItem> toOrderItemEntity(CreateLimitedOrderResquestV1 req) {
		List<OrderItem> orderItems = new ArrayList<>();

		req.getItems().forEach(itemReq -> {
			OrderItem orderItem = OrderItem.builder()
				.optionId(itemReq.getOptionId())
				.itemId(itemReq.getItemId())
				.productType(itemReq.getProductType())
				.productName(itemReq.getProductName())
				.brandName(itemReq.getBrandName())
				.sellerId(itemReq.getSellerId())
				.productColor(itemReq.getProductColor())
				.productSize(itemReq.getProductSize())
				.productPrice(itemReq.getProductPrice())
				.productAmount(itemReq.getProductAmount())
				.totalProductPrice(itemReq.getTotalProductPrice())
				.build();

			orderItems.add(orderItem);
		});

		return orderItems;
	}
}
