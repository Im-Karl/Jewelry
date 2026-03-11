package com.example.jewelry.order.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.shared.enums.OrderStatus;
import com.example.jewelry.shared.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Link tới User (người đặt)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // --- Thông tin giao hàng ---
    private String shippingAddress;
    private String recipientPhone;
    private String recipientName;

    private boolean gift;
    private String giftMessage;

    // Audit
    private LocalDateTime createdAt = LocalDateTime.now();

    // Quan hệ 1-N với OrderItem
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items;

    private String couponCode;       // Mã giảm giá đã dùng (nếu có)
    private BigDecimal discountAmount; // Số tiền được giảm (VD: 50.000)
    private int pointsUsed; // Số điểm đã trừ (VD: 5000)
    private BigDecimal pointsDiscountAmount;
    private BigDecimal finalAmount;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}