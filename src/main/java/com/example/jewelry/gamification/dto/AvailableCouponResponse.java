package com.example.jewelry.gamification.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AvailableCouponResponse {
    private String code;
    private int discountPercent;
    private String type;
    private LocalDateTime expiredAt;
    private String description;     // Mô tả (VD: "Mã toàn sàn", "Mã trúng thưởng")
}