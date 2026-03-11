package com.example.jewelry.product.web;

import com.example.jewelry.product.dto.*;
import com.example.jewelry.shared.response.MessageResponse;
import com.example.jewelry.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // API cho Chatbot gọi để lấy gợi ý
    @GetMapping("/consultation")
    public ResponseEntity<List<ProductDto>> getFengShuiAdvice(@RequestParam String element) {
        return ResponseEntity.ok(productService.getProductsByFengShui(element));
    }

    @PostMapping(consumes = {"multipart/form-data"}) // Chỉ định nhận form-data
    public ResponseEntity<ProductDto> createProduct(@ModelAttribute CreateProductRequest request) {
        // TODO: Kiểm tra quyền Admin ở đây (sẽ làm ở bước Security)
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                new MessageResponse("Xóa sản phẩm thành công")
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<ProductDto>> getProductsWithFilter(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(productService.getProductsWithFilter(
                search, categoryId, minPrice, maxPrice, page, size, sortBy, sortDir));
    }


    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
     @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String id,
            @ModelAttribute UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> toggleStatus(
            @PathVariable String id,
            @RequestParam boolean isDeleted) {
        productService.toggleProductStatus(id, isDeleted);
        String msg = isDeleted ? "Đã ẩn sản phẩm" : "Đã khôi phục sản phẩm";
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/{id}/variants")
    public ResponseEntity<ProductVariantDto> addVariant(
            @PathVariable String id,
            @RequestBody @Valid CreateVariantRequest request) {
        return ResponseEntity.ok(productService.addVariant(id, request));
    }

    @PutMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ProductVariantDto> updateVariant(
            @PathVariable String productId,
            @PathVariable UUID variantId,
            @RequestBody @Valid UpdateVariantRequest request) {
        return ResponseEntity.ok(productService.updateVariant(productId, variantId, request));
    }

    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<String> deleteVariant(
            @PathVariable String productId,
            @PathVariable UUID variantId) {
        productService.deleteVariant(productId, variantId);
        return ResponseEntity.ok("Đã xóa biến thể thành công");
    }
}