package com.example.jewelry.product.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String iconUrl; // URL ảnh trên Supabase
}