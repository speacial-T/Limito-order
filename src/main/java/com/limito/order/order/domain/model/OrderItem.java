package com.limito.order.order.domain.model;

import java.util.UUID;

import com.limito.order.common.ProductType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_items")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "order_item_id", columnDefinition = "uuid")
	private UUID id;

	// 연관관계의 주인
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Column(name = "product_option_id", nullable = false)
	private UUID optionId;

	@Column(name = "product_item_id")
	private UUID productItemId;

	@Column(name = "product_stock_id")
	private UUID stockId;

	@Column(name = "product_id")
	private UUID productId;

	@Column(name = "product_type", nullable = false)
	private ProductType productType;

	@Column(name = "product_name", nullable = false, length = 100)
	private String productName;

	@Column(name = "brand_name", nullable = false, length = 100)
	private String brandName;

	@Column(name = "seller_id", nullable = false)
	private Long sellerId;

	@Column(name = "product_color", nullable = false, length = 50)
	private String productColor;

	@Column(name = "product_size", nullable = false, length = 10)
	private String productSize;

	@Column(name = "product_price", nullable = false)
	private int productPrice;

	@Column(name = "product_amount", nullable = false)
	private int productAmount;

	@Column(name = "total_product_price")
	private Long totalProductPrice;

	public void attachOrder(Order order) {
		this.order = order;
	}
}
