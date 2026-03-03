package com.example.jewelry.product.web;

import com.example.jewelry.product.dto.CategoryDto;
import com.example.jewelry.product.dto.CreateCategoryRequest;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryById(Long id);
    CategoryDto createCategory(CreateCategoryRequest request);
    void deleteCategory(Long id);
}