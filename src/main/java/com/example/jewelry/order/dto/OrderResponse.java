package com.example.jewelry.order.dto;

import com.example.jewelry.shared.enums.OrderStatus;
import com.example.jewelry.shared.enums.PaymentMethod;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private boolean isGift;
    private String giftMessage;
    private LocalDateTime createdAt;

    private String userName;
    private String userEmail;

    private String couponCode;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;

    private List<OrderItemResponse> items;
}