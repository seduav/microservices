package org.example.orderservice.controller;

import org.example.events.OrderEvent;
import org.example.orderservice.dto.OrderRequest;
import org.example.orderservice.service.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer producer;

    @PostMapping
    public OrderEvent createOrder(@RequestBody OrderRequest request){

        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                request.getCustomerId(),
                request.getProduct(),
                request.getQuantity(),
                LocalDateTime.now().toString()
        );

        producer.publish(event);

        return event;
    }

}