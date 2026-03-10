package com.example.jewelry.product.web;

import com.example.jewelry.product.dto.CreateProductRequest;
import com.example.jewelry.product.dto.ProductDto;
import com.example.jewelry.product.dto.UpdateProductRequest;
import com.example.jewelry.shared.response.PageResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(String id);
    List<ProductDto> getProductsByFengShui(String element);
    ProductDto createProduct(CreateProductRequest request);
    void deleteProduct(String id);
    PageResponse<ProductDto> getProductsWithFilter(String search, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, int page, int size, String sortBy, String sortDir);
    ProductDto updateProduct(String id, UpdateProductRequest request);

    // 3. Ẩn/Hiện sản phẩm (Thay vì xóa hẳn khỏi DB)
    void toggleProductStatus(String id, boolean isDeleted);
}