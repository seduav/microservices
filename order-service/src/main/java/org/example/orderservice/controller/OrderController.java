package org.example.orderservice.controller;

import jakarta.validation.Valid;
import org.example.events.OrderEvent;
import org.example.orderservice.dto.OrderRequest;
import org.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderEvent createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        return orderService.createOrder(request);
    }
}