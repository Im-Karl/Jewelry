package com.example.jewelry.gamification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublicCouponRepository
        extends JpaRepository<PublicCoupon, UUID> {

    Optional<PublicCoupon> findByCodeAndActiveTrue(String code);

    boolean existsByCode(String code);

    @Query("SELECT c FROM PublicCoupon c WHERE c.active = true " +
            "AND (c.expiredAt IS NULL OR c.expiredAt >= CURRENT_TIMESTAMP) " +
            "AND (c.startAt IS NULL OR c.startAt <= CURRENT_TIMESTAMP) " +
            "AND (c.maxUsage = -1 OR c.usedCount < c.maxUsage)")
    List<PublicCoupon> findAvailablePublicCoupons();
}
