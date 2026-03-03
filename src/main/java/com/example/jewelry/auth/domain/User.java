package com.example.jewelry.auth.domain;

import com.example.jewelry.shared.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;
    private String phoneNumber;
    private String avatarUrl;

    // --- Thông tin Phong Thủy & Cá nhân hóa ---
    private LocalDate birthDate;
    private String gender; // male, female, other
    private String zodiacSign; // Cung hoàng đạo (Sẽ tính toán sau)
    private String fengShuiElement; // Mệnh (Kim, Mộc...)

    // --- Loyalty ---
    private int loyaltyPoints = 0;
    private String membershipTier = "SILVER";

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private boolean deleted = false;

    // Audit fields (Optional nhưng nên có)
    @Column(updatable = false)
    private LocalDate createdAt = LocalDate.now();
}