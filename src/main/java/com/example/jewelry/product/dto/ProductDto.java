package com.example.jewelry.product.dto;

import lombok.Data;
import java.math.BigDecimal;

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
    private String categoryName; // Chỉ cần tên để hiển thị
}