package org.example.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequest {

    private Long customerId;

    @NotNull
    private String product;

    @NotNull
    @Positive
    private Integer quantity;

}