package com.example.jewelry.gamification.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "public_coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code; // VD: NOEL2026, TET50

    private int discountPercent;

    private int maxUsage;      // Tổng lượt dùng  || maxUsage = -1 → không giới hạn
    private int usedCount;     // Đã dùng bao nhiêu lần

    private LocalDateTime startAt;
    private LocalDateTime expiredAt;

    private boolean active;
}

