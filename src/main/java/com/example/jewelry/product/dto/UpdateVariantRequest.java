package com.example.jewelry.product.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateVariantRequest {
    private BigDecimal additionalPrice;

    @Min(value = 0, message = "Tồn kho không được là số âm")
    private Integer stockQuantity;
}
