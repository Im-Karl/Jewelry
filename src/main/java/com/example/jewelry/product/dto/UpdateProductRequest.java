package com.example.jewelry.product.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String materialType;
    private String stoneType;
    private String platingColor;
    private String fengShuiElement;

    private Long categoryId;

    private MultipartFile newImage;
}
