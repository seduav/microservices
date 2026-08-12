package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Order(
            UUID id,
            Long customerId,
            String product,
            Integer quantity
    ) {
        this.id = id;
        this.customerId = customerId;
        this.product = product;
        this.quantity = quantity;
        this.status = OrderStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }
}