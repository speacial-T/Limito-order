package com.limito.order.order.domain.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderItemResponseV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;

@Component
public class OrderMapper {

	public Order toOrderEntity(Long userId, CreateLimitedOrderRequestV1 req) {
		return Order.builder()
			.userId(userId)
			.receiverName(req.getReceiverName())
			.phoneNumber(req.getPhoneNumber())
			.deliveryAddress(req.getDeliveryAddress())
			.totalPrice(req.getTotalPrice())
			.orderStatus(com.limito.order.common.OrderStatus.ORDER_FINISH)
			.successedAt(LocalDateTime.now())
			.build();
	}

	public List<OrderItem> toOrderItemEntity(CreateLimitedOrderRequestV1 req) {
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

	public CreateLimitedOrderResponseV1 toLimitedOrderResponse(Order order) {
		// List<OrderItem> orderItems = order.getOrderItems();
		List<CreateLimitedOrderItemResponseV1> limitedOrderItemResponses = toLimitedOrderItemResponse(order);

		return CreateLimitedOrderResponseV1.builder()
			.orderId(order.getId())
			.userId(order.getUserId())
			.receiverName(order.getReceiverName())
			.phoneNumber(order.getPhoneNumber())
			.deliveryAddress(order.getDeliveryAddress())
			.totalPrice(order.getTotalPrice())
			.orderStatus(order.getOrderStatus())
			.successedAt(order.getSuccessedAt())
			.itemSummary(order.getItemSummary())
			.items(limitedOrderItemResponses)
			.build();
	}

	public List<CreateLimitedOrderItemResponseV1> toLimitedOrderItemResponse(Order order) {
		List<OrderItem> orderItems = order.deliverOrderItems(order);
		List<CreateLimitedOrderItemResponseV1> responses = new ArrayList<>();

		orderItems.forEach(orderItem -> {
			CreateLimitedOrderItemResponseV1 res = CreateLimitedOrderItemResponseV1.builder()
				.orderItemId(orderItem.getId())
				.optionId(orderItem.getOptionId())
				.itemId(orderItem.getItemId())
				.productType(orderItem.getProductType())
				.productName(orderItem.getProductName())
				.brandName(orderItem.getBrandName())
				.sellerId(orderItem.getSellerId())
				.productColor(orderItem.getProductColor())
				.productSize(orderItem.getProductSize())
				.productPrice(orderItem.getProductPrice())
				.productAmount(orderItem.getProductAmount())
				.totalProductPrice(orderItem.getTotalProductPrice())
				.build();

			responses.add(res);
		});

		return responses;
	}

}
