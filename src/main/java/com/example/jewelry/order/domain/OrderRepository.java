package com.example.jewelry.order.domain;

import com.example.jewelry.shared.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserId(UUID userId);
    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
            "FROM OrderItem oi " +
            "WHERE oi.order.user.id = :userId " +
            "AND oi.product.id = :productId " +
            "AND oi.order.status = 'COMPLETED'")
    boolean hasUserPurchasedProduct(@Param("userId") UUID userId, @Param("productId") String productId);

    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status IN ('PAID', 'SHIPPING', 'COMPLETED')")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startOfDay")
    long countOrdersFrom(@Param("startOfDay") LocalDateTime startOfDay);

    List<Order> findByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime time);
}