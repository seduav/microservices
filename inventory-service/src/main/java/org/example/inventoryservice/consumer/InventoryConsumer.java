package org.example.inventoryservice.consumer;

import lombok.RequiredArgsConstructor;
import org.example.events.InventoryResult;
import org.example.events.OrderEvent;
import org.example.events.InventoryStatus;
import org.example.inventoryservice.producer.InventoryProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryProducer inventoryProducer;

    @KafkaListener(
            topics = "order-created",
            groupId = "inventory-group"
    )
    public void consume(OrderEvent order) {

        System.out.println("----------------");
        System.out.println("Processing order:");
        System.out.println("OrderId: " + order.getOrderId());
        System.out.println("Product: " + order.getProduct());
        System.out.println("Quantity: " + order.getQuantity());
        System.out.println("----------------");

        InventoryStatus status;

        if (order.getQuantity() <= 5) {
            status = InventoryStatus.AVAILABLE;
        } else {
            status = InventoryStatus.OUT_OF_STOCK;
        }

        InventoryResult result = new InventoryResult(
                order.getOrderId(),
                status
        );

        inventoryProducer.publish(result);
    }
}