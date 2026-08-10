package org.example.inventoryservice.consumer;

import org.example.events.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    public void consume(OrderEvent order){
        System.out.println("");
        System.out.println("----------------");
        System.out.println("Processing order:");
        System.out.println("OrderId: " + order.getOrderId());
        System.out.println("Product: " + order.getProduct());
        System.out.println("Quantity: " + order.getQuantity());
        System.out.println("----------------");

    }

}