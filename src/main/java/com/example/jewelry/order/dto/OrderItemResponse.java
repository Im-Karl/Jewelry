package com.example.jewelry.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemResponse {
    private UUID id;
    private String productId;
    private String productName;
    private String productMainImageUrl;
    private int quantity;
    private BigDecimal priceAtPurchase;
}