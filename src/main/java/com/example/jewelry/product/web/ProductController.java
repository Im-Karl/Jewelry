package com.example.jewelry.product.web;

import com.example.jewelry.product.dto.CreateProductRequest;
import com.example.jewelry.product.dto.ProductDto;
import com.example.jewelry.shared.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}