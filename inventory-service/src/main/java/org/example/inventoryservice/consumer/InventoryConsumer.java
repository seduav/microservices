package org.example.inventoryservice.consumer;

import lombok.RequiredArgsConstructor;
import org.example.events.InventoryResult;
import org.example.events.OrderEvent;
import org.example.events.InventoryStatus;
import org.example.inventoryservice.entity.ProcessedMessage;
import org.example.inventoryservice.producer.InventoryProducer;
import org.example.inventoryservice.repository.ProcessedMessageRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryProducer inventoryProducer;

    private final ProcessedMessageRepository processedMessageRepository;

    private final Random random = new Random();

    @KafkaListener(
            topics = "order-created",
            groupId = "inventory-group"
    )
    public void consume(OrderEvent order) {

        String orderId = order.getOrderId().toString();

        if (processedMessageRepository.existsByMessageId(orderId)) {
            System.out.println("==============================");
            System.out.println("Duplicate order ignored: " + orderId);
            System.out.println("==============================");
            return;
        } else {
            System.out.println("==============================");
            System.out.println("Processing order:");
            System.out.println("OrderId: " + order.getOrderId());
            System.out.println("Product: " + order.getProduct());
            System.out.println("Quantity: " + order.getQuantity());
            System.out.println("==============================");
        }

        if (random.nextBoolean()) {
            throw new RuntimeException(
                    "Simulated inventory processing failure"
            );
        }

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

        System.out.println(
                "Inventory result published for order: "
                        + orderId
        );

        processedMessageRepository.save(new ProcessedMessage(orderId));
    }
}