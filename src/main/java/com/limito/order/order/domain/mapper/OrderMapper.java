package com.limito.order.order.domain.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.limito.order.common.OrderStatus;
import com.limito.order.order.domain.dto.feignclient.limited.CancelReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.GetOrderedProductInfoRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.ItemAmountRequest;
import com.limito.order.order.domain.dto.feignclient.limited.OptionItemAmountRequest;
import com.limito.order.order.domain.dto.feignclient.limited.ProductOptionItemRequest;
import com.limito.order.order.domain.dto.feignclient.limited.RollbackStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.request.ProductInfosGetRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.request.StockRollbackRequest;
import com.limito.order.order.domain.dto.request.CreateLimitedOrderItemRequestV1;
import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.request.CreateResellOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderItemResponseV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderItemResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderResponseV1;
import com.limito.order.order.domain.dto.response.GetOrderForCompanyResponseV1;
import com.limito.order.order.domain.dto.response.GetOrderForUserResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForCompanyResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForUserResponseV1;
import com.limito.order.order.domain.dto.response.OrderInfoResponse;
import com.limito.order.order.domain.dto.response.OrderItemInfoResponse;
import com.limito.order.order.domain.model.CompanyOrder;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;

@Component
public class OrderMapper {

	public Order toOrderEntity(Long userId, CreateLimitedOrderRequestV1 req) {
		return Order.builder()
			.userId(userId)
			.orderProductType(req.getOrderProductType())
			.orderStatus(OrderStatus.ORDER_PENDING)
			.build();
	}

	public Order toOrderEntity(Long userId, CreateResellOrderRequestV1 req) {
		return Order.builder()
			.userId(userId)
			.orderProductType(req.getOrderProductType())
			.orderStatus(OrderStatus.ORDER_PENDING)
			.build();
	}

	public List<OrderItem> toOrderItemEntity(CreateLimitedOrderRequestV1 req) {
		List<OrderItem> orderItems = new ArrayList<>();

		req.getItems().forEach(itemReq -> {
			OrderItem orderItem = OrderItem.builder()
				.optionId(itemReq.getOptionId())
				.productItemId(itemReq.getProductItemId())
				.productAmount(itemReq.getProductAmount())
				.build();

			orderItems.add(orderItem);
		});

		return orderItems;
	}

	public List<OrderItem> toOrderItemEntity(CreateResellOrderRequestV1 req) {
		List<OrderItem> orderItems = new ArrayList<>();

		req.getItems().forEach(itemReq -> {
			OrderItem orderItem = OrderItem.builder()
				.optionId(itemReq.getOptionId())
				.stockId(itemReq.getStockId())
				.productId(itemReq.getProductId())
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
			.orderProductType(order.getOrderProductType())
			.totalPrice(order.getTotalPrice())
			.orderStatus(order.getOrderStatus())
			.successedAt(order.getSuccessedAt())
			.itemSummary(order.getItemSummary())
			.items(limitedOrderItemResponses)
			.build();
	}

	public List<CreateLimitedOrderItemResponseV1> toLimitedOrderItemResponse(Order order) {
		List<OrderItem> orderItems = order.getOrderItems();
		List<CreateLimitedOrderItemResponseV1> responses = new ArrayList<>();

		orderItems.forEach(orderItem -> {
			CreateLimitedOrderItemResponseV1 res = CreateLimitedOrderItemResponseV1.builder()
				.orderItemId(orderItem.getId())
				.optionId(orderItem.getOptionId())
				.productItemId(orderItem.getProductItemId())
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

	public CreateResellOrderResponseV1 toResellOrderResponse(Order order) {
		List<CreateResellOrderItemResponseV1> resellOrderItemResponses = toResellOrderItemResponse(order);

		return CreateResellOrderResponseV1.builder()
			.orderId(order.getId())
			.userId(order.getUserId())
			.receiverName(order.getReceiverName())
			.phoneNumber(order.getPhoneNumber())
			.deliveryAddress(order.getDeliveryAddress())
			.orderProductType(order.getOrderProductType())
			.totalPrice(order.getTotalPrice())
			.orderStatus(order.getOrderStatus())
			.successedAt(order.getSuccessedAt())
			.itemSummary(order.getItemSummary())
			.items(resellOrderItemResponses)
			.build();
	}

	public List<CreateResellOrderItemResponseV1> toResellOrderItemResponse(Order order) {
		List<OrderItem> orderItems = order.getOrderItems();
		List<CreateResellOrderItemResponseV1> responses = new ArrayList<>();

		orderItems.forEach(orderItem -> {
			CreateResellOrderItemResponseV1 res = CreateResellOrderItemResponseV1.builder()
				.orderItemId(orderItem.getId())
				.optionId(orderItem.getOptionId())
				.stockId(orderItem.getStockId())
				.productId(orderItem.getProductId())
				.productType(orderItem.getProductType())
				.productName(orderItem.getProductName())
				.brandName(orderItem.getBrandName())
				.sellerId(orderItem.getSellerId())
				.productColor(orderItem.getProductColor())
				.productSize(orderItem.getProductSize())
				.productPrice(orderItem.getProductPrice())
				.build();

			responses.add(res);
		});

		return responses;
	}

	public GetOrdersForUserResponseV1 toGetOrdersForUserResponse(Order order) {
		return GetOrdersForUserResponseV1.builder()
			.orderId(order.getId())
			.itemSummary(order.getItemSummary())
			.productType(
				(order.getOrderItems().isEmpty()) ? null :
					order.getOrderItems()
						.get(0)
						.getProductType()
						.name()
			)
			.totalPrice(order.getTotalPrice())
			.successedAt(order.getSuccessedAt())
			.build();
	}

	public GetOrdersForCompanyResponseV1 toGetOrdersForCompanyResponse(CompanyOrder companyOrder) {
		return GetOrdersForCompanyResponseV1.builder()
			.orderId(companyOrder.getOrderId())
			.itemSummary(companyOrder.getItemSummary())
			.totalPrice(companyOrder.getTotalPrice())
			.successedAt(companyOrder.getSuccessedAt())
			.build();
	}

	public GetOrderForUserResponseV1 toGetOrderForUserResponse(Order order) {
		return GetOrderForUserResponseV1.builder()
			.order(toOrderInfoResponse(order))
			.orderItems(toOrderItemInfoResponseList(order.getOrderItems()))
			.build();
	}

	public GetOrderForCompanyResponseV1 toGetOrderForCompanyResponse(Order order, Long userId) {
		return GetOrderForCompanyResponseV1.builder()
			.order(toOrderInfoResponse(order))
			.orderItems(toOrderItemInfoResponseList(order.getOrderItems(), userId))
			.build();
	}

	public OrderInfoResponse toOrderInfoResponse(Order order) {
		return OrderInfoResponse.builder()
			.orderId(order.getId())
			.userId(order.getUserId())
			.receiverName(order.getReceiverName())
			.phoneNumber(order.getPhoneNumber())
			.deliveryAddress(order.getDeliveryAddress())
			.totalPrice(order.getTotalPrice())
			.orderStatus(order.getOrderStatus().name())
			.cancelReason(order.getCancelReason())
			.successedAt(order.getSuccessedAt())
			.itemSummary(order.getItemSummary())
			.build();
	}

	public List<OrderItemInfoResponse> toOrderItemInfoResponseList(List<OrderItem> orderItems) {
		return orderItems.stream()
			.map(orderItem -> OrderItemInfoResponse.builder()
				.id(orderItem.getId())
				.optionId(orderItem.getOptionId())
				.productItemId(orderItem.getProductItemId())
				.stockId(orderItem.getStockId())
				.productId(orderItem.getProductId())
				.productType(orderItem.getProductType().name())
				.productName(orderItem.getProductName())
				.brandName(orderItem.getBrandName())
				.sellerId(orderItem.getSellerId())
				.productColor(orderItem.getProductColor())
				.productSize(orderItem.getProductSize())
				.productPrice(orderItem.getProductPrice())
				.productAmount(orderItem.getProductAmount())
				.totalProductPrice(orderItem.getTotalProductPrice())
				.build())
			.toList();
	}

	public List<OrderItemInfoResponse> toOrderItemInfoResponseList(List<OrderItem> orderItems, Long userId) {
		return orderItems.stream()
			.filter(orderItem -> userId.equals(orderItem.getSellerId()))
			.map(orderItem -> OrderItemInfoResponse.builder()
				.id(orderItem.getId())
				.optionId(orderItem.getOptionId())
				.productItemId(orderItem.getProductItemId())
				.stockId(orderItem.getStockId())
				.productId(orderItem.getProductId())
				.productType(orderItem.getProductType().name())
				.productName(orderItem.getProductName())
				.brandName(orderItem.getBrandName())
				.sellerId(orderItem.getSellerId())
				.productColor(orderItem.getProductColor())
				.productSize(orderItem.getProductSize())
				.productPrice(orderItem.getProductPrice())
				.productAmount(orderItem.getProductAmount())
				.totalProductPrice(orderItem.getTotalProductPrice())
				.build())
			.toList();
	}

	public CancelReserveStockRequestV1 reserveCancelRequest(List<OrderItem> orderItems) {
		List<ItemAmountRequest> items = orderItems.stream()
			.map(orderItem -> ItemAmountRequest.builder()
				.limitedProductItemId(orderItem.getProductItemId())
				.amount(orderItem.getProductAmount())
				.build())
			.toList();
		return new CancelReserveStockRequestV1(items);
	}

	public List<UUID> getStockIds(List<OrderItem> orderItems) {
		return orderItems.stream()
			.map(OrderItem::getStockId)
			.toList();
	}

	public RollbackStockRequestV1 toRollbackStockRequest(List<OrderItem> orderItems) {
		List<OptionItemAmountRequest> request = orderItems.stream()
			.map(orderItem -> OptionItemAmountRequest.builder()
				.limitedProductOptionId(orderItem.getOptionId())
				.limitedProductItemId(orderItem.getProductItemId())
				.amount(orderItem.getProductAmount())
				.build())
			.toList();

		return RollbackStockRequestV1.builder()
			.products(request).build();
	}

	public List<StockRollbackRequest> toStockRollbackRequest(List<OrderItem> orderItems) {
		return orderItems.stream().map(orderItem -> StockRollbackRequest.builder()
				.productId(orderItem.getProductId())
				.optionId(orderItem.getOptionId())
				.stockId(orderItem.getStockId())
				.build())
			.toList();
	}

	public List<ProductInfosGetRequestV1> toProductInfosGetRequests(List<OrderItem> orderItems) {
		return orderItems.stream().map(orderItem -> ProductInfosGetRequestV1.builder()
				.productId(orderItem.getProductId())
				.optionId(orderItem.getOptionId())
				.stockId(orderItem.getStockId())
				.build())
			.toList();
	}

	public GetOrderedProductInfoRequestV1 toGetOrderedProductInfoRequest(
		CreateLimitedOrderRequestV1 createLimitedOrderRequest) {
		List<CreateLimitedOrderItemRequestV1> orderItemRequests = createLimitedOrderRequest.getItems();
		List<ProductOptionItemRequest> products = orderItemRequests.stream()
			.map(createLimitedOrderItemRequestV1 -> ProductOptionItemRequest.builder()
				.limitedProductId(createLimitedOrderItemRequestV1.getProductId())
				.limitedProductOptionId(createLimitedOrderItemRequestV1.getOptionId())
				.limitedProductItemId(createLimitedOrderItemRequestV1.getProductItemId())
				.build())
			.toList();

		return GetOrderedProductInfoRequestV1.builder()
			.products(products)
			.build();
	}
}
