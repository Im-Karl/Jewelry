package com.example.jewelry.warranty.domain;

import com.example.jewelry.order.domain.OrderItem;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "warranties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warranty {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(unique = true, nullable = false)
    private String warrantyCode; // Mã tra cứu (VD: BH-123456)

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private String status; // ACTIVE, EXPIRED, VOID

    // Thông tin người mua (để tra cứu nhanh mà không cần join quá nhiều)
    private String customerPhone;
}