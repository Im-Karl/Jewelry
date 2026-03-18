package com.example.jewelry.gamification.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreatePublicCouponRequest {
    private String code;
    private BigDecimal discountAmount;
    private int maxUsage;
    private LocalDateTime expiredAt;
}
