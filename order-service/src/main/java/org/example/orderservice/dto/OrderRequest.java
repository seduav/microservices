package org.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderRequest {

    private Long customerId;
    private String product;
    private Integer quantity;

}