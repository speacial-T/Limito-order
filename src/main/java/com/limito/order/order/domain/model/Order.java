package com.limito.order.order.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.limito.order.common.OrderStatus;
import com.limito.order.order.domain.dto.request.CreateLimitedOrderResquestV1;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_orders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "order_id", columnDefinition = "uuid")
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "receiver_name", nullable = false, length = 10)
	private String receiverName;

	@Column(name = "phone_number", nullable = false, length = 20)
	private String phoneNumber;

	@Column(name = "delivery_address", nullable = false)
	private String deliveryAddress;

	@Column(name = "total_price", nullable = false)
	private Long totalPrice;

	@Column(name = "order_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@Column(name = "cancel_reason")
	private String cancelReason;

	@Column(name = "successed_at", nullable = false)
	private LocalDateTime successedAt;

	@Column(name = "item_summary", nullable = false, length = 100)
	private String itemSummary;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OrderItem> orderItems = new ArrayList<>();

	public void attachOrderItems(List<OrderItem> orderItems) {
		orderItems.forEach(orderItem -> {
			this.orderItems.add(orderItem);
			orderItem.attachOrder(this);
		});
	}

	public void attachSummary(CreateLimitedOrderResquestV1 req) {
		int itemCount = req.getItems().size() - 1;
		String firstProductName = req.getItems().get(0).getProductName();
		this.itemSummary = firstProductName + " 외 " + itemCount + "건";
	}

	public List<OrderItem> deliverOrderItems(Order order) {
		return order.getOrderItems();
	}
}
