package org.example.orderservice.consumer;

import lombok.RequiredArgsConstructor;
import org.example.events.InventoryResult;
import org.example.events.InventoryStatus;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryResultConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "inventory-result",
            groupId = "order-group",
            containerFactory = "inventoryKafkaListenerContainerFactory"
    )
    public void consume(InventoryResult result) {

        System.out.println("Inventory result received:");
        System.out.println("OrderId: " + result.getOrderId());
        System.out.println("Status: " + result.getStatus());

        UUID orderId = UUID.fromString(result.getOrderId());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + result.getOrderId()
                        )
                );

        if (result.getStatus() == InventoryStatus.AVAILABLE) {
            order.setStatus(OrderStatus.AVAILABLE);
        } else if (result.getStatus() == InventoryStatus.OUT_OF_STOCK) {
            order.setStatus(OrderStatus.OUT_OF_STOCK);
        }

        orderRepository.save(order);
    }
}