package com.example.jewelry.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserId(UUID userId);
    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
            "FROM OrderItem oi " +
            "WHERE oi.order.user.id = :userId " +
            "AND oi.product.id = :productId " +
            "AND oi.order.status = 'SHIPPING'")
    boolean hasUserPurchasedProduct(@Param("userId") UUID userId, @Param("productId") String productId);
}