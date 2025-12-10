package com.limito.order.order.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.limito.order.common.OrderStatus;
import com.limito.order.order.domain.model.CompanyOrder;
import com.limito.order.order.domain.model.Order;

public interface OrderRepositoryV1 extends JpaRepository<Order, UUID> {

	Slice<Order> findAllByUserIdAndOrderStatusNotOrderBySuccessedAtDesc(
		Long userId,
		OrderStatus orderStatus,
		Pageable pageable
	);

	@Query("""
			SELECT new com.limito.order.order.domain.model.CompanyOrder(
				o.id,
				o.itemSummary,
				o.totalPrice,
				o.successedAt
			)
			FROM Order o
			WHERE o.orderStatus <> :orderStatus
				AND EXISTS (
					SELECT 1
					FROM OrderItem i
					WHERE i.order = o
						AND i.sellerId = :userId
				)
			ORDER BY o.successedAt DESC
		""")
	Slice<CompanyOrder> findAllBySellerIdAndOrderStatusNot(Long userId, OrderStatus orderStatus, Pageable pageable);

	Optional<Order> findByIdAndUserId(UUID id, Long userId);

	Optional<Order> findByIdAndOrderStatusNotAndOrderItems_SellerId(UUID id, OrderStatus orderStatus, Long userId);
}
