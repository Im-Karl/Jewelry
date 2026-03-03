package com.example.jewelry.payment.domain;

import com.example.jewelry.order.domain.Order;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String paymentMethod; // VNPAY, MOMO
    private String transactionCode; // Mã giao dịch từ cổng thanh toán trả về
    private BigDecimal amount;
    private String status; // PENDING, SUCCESS, FAILED

    private LocalDateTime createdAt;
}