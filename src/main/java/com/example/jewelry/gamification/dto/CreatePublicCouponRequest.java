package com.example.jewelry.gamification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreatePublicCouponRequest {
    private String code;
    private int discountPercent;
    private int maxUsage;
    private LocalDateTime expiredAt;
}
