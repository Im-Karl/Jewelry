package com.example.jewelry.gamification.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wheel_prizes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WheelPrize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label; // Tên hiển thị: "Giảm 10%", "Chúc bạn may mắn"

    private double probability; // Tỷ lệ trúng (0.0 - 1.0). VD: 0.1 là 10%

    private boolean isWinning; // Có phải là trúng thưởng không?
    private String couponCodePrefix; // Tiền tố mã: SALE10, GIFT...
    private int discountPercent; // Giá trị giảm (nếu có)

    private boolean active;
}