package com.example.jewelry.gamification.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PublicCouponResponse {

    private UUID id;
    private String code;

    private BigDecimal discountAmount;

    private int maxUsage;
    private int usedCount;

    private LocalDateTime startAt;
    private LocalDateTime expiredAt;

    private boolean active;
}
