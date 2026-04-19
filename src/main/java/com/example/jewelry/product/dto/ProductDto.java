package com.example.jewelry.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String id;
    private String name;
    private BigDecimal basePrice;
    private String mainImageUrl;
    private String materialType;
    private String stoneType;
    private String fengShuiElement;
    private boolean isArEnabled;
    private Long categoryId;
    private String categoryName;
    private String description;
    private String platingColor;

    private int soldQuantity;

    private List<ProductVariantDto> variants;

    @JsonIgnore
    public int getTotalStock() {
        if (variants == null || variants.isEmpty()) {
            return 0;
        }
        return variants.stream()
                .mapToInt(ProductVariantDto::getStockQuantity)
                .sum();
    }
}