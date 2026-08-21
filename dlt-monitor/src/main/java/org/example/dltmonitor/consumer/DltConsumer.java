package org.example.dltmonitor.consumer;

import lombok.RequiredArgsConstructor;
import org.example.events.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DltConsumer {

    @KafkaListener(
            topics = "order-created.DLT",
            groupId = "dlt-monitor"
    )
    public void consume(OrderEvent order) {
        System.out.println("==============================");
        System.out.println("FAILED MESSAGE FROM DLT");
        System.out.println("==============================");
        System.out.println("OrderId: " + order.getOrderId());
        System.out.println("CustomerId: " + order.getCustomerId());
        System.out.println("Product: " + order.getProduct());
        System.out.println("Quantity: " + order.getQuantity());
        System.out.println("==============================");
    }
}