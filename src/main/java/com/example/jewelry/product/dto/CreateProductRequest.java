package com.example.jewelry.product.dto;

import com.example.jewelry.shared.constants.ErrorMessage;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private BigDecimal basePrice;

    // Tech Specs
    private String materialType;
    private String stoneType;
    private String platingColor;

    // Phong Thủy
    private String fengShuiElement;

    // File ảnh upload lên
    private MultipartFile image;

    private Long categoryId;

    @Min(value = 0, message = ErrorMessage.STOCK_NOT_NEGATIVE)
    private int stockQuantity;
}