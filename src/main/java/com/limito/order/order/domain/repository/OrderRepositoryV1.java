package com.limito.order.order.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.limito.order.order.domain.model.Order;

public interface OrderRepositoryV1 extends JpaRepository<Order, UUID> {
}
