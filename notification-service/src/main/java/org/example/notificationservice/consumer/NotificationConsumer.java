package org.example.notificationservice.consumer;

import org.example.events.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-service"
    )
    public void consume(OrderEvent event) {
        System.out.println("==============================");
        System.out.println("Email sent to customer");
        System.out.println("CustomerId: " + event.getCustomerId());
        System.out.println("OrderId: " + event.getOrderId());
        System.out.println("Product: " + event.getProduct());
        System.out.println("Quantity: " + event.getQuantity());
        System.out.println("==============================");
    }
}