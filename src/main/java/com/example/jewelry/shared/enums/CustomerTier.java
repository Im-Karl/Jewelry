package com.example.jewelry.shared.enums;

import lombok.Getter;

@Getter
public enum CustomerTier {
    MEMBER(0),      // 0 - 50 triệu
    SILVER(3),      // 50 - 200 triệu
    GOLD(5),        // 200 - 800 triệu
    DIAMOND(8),     // 800 triệu - 2 tỷ
    ELITE(10),      // 2 - 5 tỷ
    ROYAL(12);      // > 5 tỷ

    private final int discountPercent;

    CustomerTier(int discountPercent) {
        this.discountPercent = discountPercent;
    }
}