package com.example.jewelry.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private String id;
    private String name;
    private BigDecimal basePrice;
    private String mainImageUrl;
    private String materialType;
    private String stoneType;
    private String fengShuiElement;
    private boolean isArEnabled;
    private String categoryName;

    private List<ProductVariantDto> variants;

    public int getTotalStock() {
        if (variants == null || variants.isEmpty()) {
            return 0;
        }
        return variants.stream()
                .mapToInt(ProductVariantDto::getStockQuantity)
                .sum();
    }
}