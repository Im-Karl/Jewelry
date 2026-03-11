package com.example.jewelry.product.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateVariantRequest {
    private String size;
    private String color;
    private BigDecimal additionalPrice;

    @Min(value = 0, message = "Tồn kho không thể là số âm")
    private int stockQuantity;

}
