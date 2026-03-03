package com.example.jewelry.gamification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCouponRepository extends JpaRepository<UserCoupon, UUID> {
    boolean existsByEmail(String email); // Để check xem email này đã chơi chưa
    Optional<UserCoupon> findByGeneratedCode(String generatedCode);
    @Query("SELECT c FROM UserCoupon c WHERE c.email = :email " +
            "AND c.isUsed = false " +
            "AND (c.expiredAt IS NULL OR c.expiredAt >= CURRENT_TIMESTAMP)")
    List<UserCoupon> findValidCouponsByEmail(String email);

}