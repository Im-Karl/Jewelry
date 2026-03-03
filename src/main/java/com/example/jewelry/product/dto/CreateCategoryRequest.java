package com.example.jewelry.product.dto;

import com.example.jewelry.shared.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateCategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    // Ảnh icon (Visual filter) - Upload lên S3
    private MultipartFile icon;
}