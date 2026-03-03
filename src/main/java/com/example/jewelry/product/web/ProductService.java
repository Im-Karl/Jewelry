package com.example.jewelry.product.web;

import com.example.jewelry.product.dto.CreateProductRequest;
import com.example.jewelry.product.dto.ProductDto;
import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(String id);
    List<ProductDto> getProductsByFengShui(String element);
    ProductDto createProduct(CreateProductRequest request);
    void deleteProduct(String id);
}