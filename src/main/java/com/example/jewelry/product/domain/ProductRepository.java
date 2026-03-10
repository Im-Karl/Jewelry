package com.example.jewelry.product.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // Query cơ bản
    List<Product> findByCategoryId(Long categoryId);

    // Query cho Chatbot Phong Thủy: Tìm sản phẩm hợp mệnh
    // Ví dụ: Mệnh Kim hợp màu Trắng, Vàng -> Tìm theo màu xi hoặc loại đá
    @Query("SELECT p FROM Product p WHERE p.fengShuiElement = :element")
    List<Product> findByFengShuiElement(String element);

    @Modifying // Báo cho Spring biết đây là câu lệnh thay đổi dữ liệu (Update/Delete)
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity " +
            "WHERE p.id = :productId AND p.stockQuantity >= :quantity")
    int deductStock(String productId, int quantity);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "AND (:categoryId = 0L OR p.category.id = :categoryId) " +
            "AND (p.basePrice >= :minPrice) " +
            "AND (p.basePrice <= :maxPrice)")
    Page<Product> filterProducts(@Param("search") String search,
                                 @Param("categoryId") Long categoryId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);
}