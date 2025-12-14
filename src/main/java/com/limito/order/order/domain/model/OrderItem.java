package com.limito.order.order.domain.model;

import java.util.UUID;

import com.limito.common.audit.BaseEntity;
import com.limito.order.common.ProductType;
import com.limito.order.order.domain.dto.feignclient.limited.GetOrderedProductInfoResponseV1;
import com.limito.order.order.domain.dto.feignclient.resell.response.ProductInfosGetResponseV1;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class OrderItem extends BaseEntity {
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

	@Column(name = "product_type")
	@Enumerated(EnumType.STRING)
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

	public UUID getLimitedProductItemId() {
		return this.productItemId;
	}

	public void attachProductInfo(GetOrderedProductInfoResponseV1.OrderedProductInfo info) {
		this.productType = ProductType.LIMITED;
		this.productName = info.getName();
		this.brandName = info.getBrandName();
		this.sellerId = info.getSellerId();
		this.productColor = info.getColor();
		this.productSize = info.getSize();
		this.productPrice = info.getPrice();
		// 수량 * 단가로 상품 총 금액 세팅
		this.totalProductPrice = (long)this.productAmount * this.productPrice;
	}

	public void attachProductInfo(ProductInfosGetResponseV1 info) {
		this.productType = ProductType.RESELL;
		this.productName = info.getProductName();
		this.brandName = info.getBrandName();
		this.sellerId = info.getSellerId();
		this.productColor = info.getProductColor();
		this.productSize = info.getProductSize();
		this.productPrice = info.getProductPrice();
		// 수량 * 단가로 상품 총 금액 세팅
		this.totalProductPrice = (long)this.productPrice;
	}
}
