package com.limito.order.order.domain.model;

import com.limito.common.entity.BaseEntity;
import com.limito.order.common.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_orders")
@NoArgsConstructor
@AllArgsConstructor
public class order extends BaseEntity {
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
    private OrderStatus OrderStatus;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "successed_at", nullable = false)
    private LocalDateTime successedAt;

    @Column(name = "item_summary", nullable = false, length = 100)
    private String itemSummary;
}
