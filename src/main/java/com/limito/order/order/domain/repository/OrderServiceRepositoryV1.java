package com.limito.order.order.domain.repository;

import com.limito.order.order.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderServiceRepositoryV1 extends JpaRepository<Order, UUID> {
}
