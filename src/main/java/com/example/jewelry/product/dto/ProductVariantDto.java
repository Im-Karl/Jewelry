package com.example.jewelry.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductVariantDto {
    private UUID id;
    private String size;
    private String color;
    private BigDecimal additionalPrice;
    private int stockQuantity;
}
