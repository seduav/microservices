package org.example.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.example.events.OrderEvent;
import org.example.orderservice.dto.OrderRequest;
import org.example.orderservice.entity.Order;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.producer.OrderProducer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public OrderEvent createOrder(OrderRequest request) {

        Order order = new Order(
                UUID.randomUUID(),
                request.getCustomerId(),
                request.getProduct(),
                request.getQuantity()
        );

        Order savedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent(
                savedOrder.getId().toString(),
                savedOrder.getCustomerId(),
                savedOrder.getProduct(),
                savedOrder.getQuantity(),
                savedOrder.getCreatedAt().toString()
        );

        orderProducer.publish(event);

        return event;
    }
}