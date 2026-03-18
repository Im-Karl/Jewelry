package com.example.jewelry.gamification.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;
    private String generatedCode; // Mã unique: SALE10-X8J2K

    private BigDecimal discountAmount;
    private boolean isUsed;

    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}