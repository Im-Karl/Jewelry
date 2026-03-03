package com.example.jewelry.consultation.service;

import org.springframework.stereotype.Component;

@Component
public class ZodiacCalculator {

    public String calculateZodiac(int day, int month) {
        return switch (month) {
            case 1 -> (day >= 20) ? "Bảo Bình" : "Ma Kết";
            case 2 -> (day >= 19) ? "Song Ngư" : "Bảo Bình";
            case 3 -> (day >= 21) ? "Bạch Dương" : "Song Ngư";
            case 4 -> (day >= 20) ? "Kim Ngưu" : "Bạch Dương";
            case 5 -> (day >= 21) ? "Song Tử" : "Kim Ngưu";
            case 6 -> (day >= 21) ? "Cự Giải" : "Song Tử";
            case 7 -> (day >= 23) ? "Sư Tử" : "Cự Giải";
            case 8 -> (day >= 23) ? "Xử Nữ" : "Sư Tử";
            case 9 -> (day >= 23) ? "Thiên Bình" : "Xử Nữ";
            case 10 -> (day >= 23) ? "Bọ Cạp" : "Thiên Bình";
            case 11 -> (day >= 22) ? "Nhân Mã" : "Bọ Cạp";
            case 12 -> (day >= 22) ? "Ma Kết" : "Nhân Mã";
            default -> "Unknown";
        };
    }
}
