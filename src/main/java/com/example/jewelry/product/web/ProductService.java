package com.example.jewelry.product.web;

import com.example.jewelry.product.dto.*;
import com.example.jewelry.shared.response.PageResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(String id);
    List<ProductDto> getProductsByFengShui(String element);
    ProductDto createProduct(CreateProductRequest request);
    void deleteProduct(String id);
    PageResponse<ProductDto> getProductsWithFilter(String search, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, int page, int size, String sortBy, String sortDir);
    ProductDto updateProduct(String id, UpdateProductRequest request);
    void toggleProductStatus(String id, boolean isDeleted);

    ProductVariantDto addVariant(String productId, CreateVariantRequest request);
    ProductVariantDto updateVariant(String productId, UUID variantId, UpdateVariantRequest request);
    void deleteVariant(String productId, UUID variantId);
}