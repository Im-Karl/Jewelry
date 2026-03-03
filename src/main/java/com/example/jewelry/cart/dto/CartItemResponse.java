package com.example.jewelry.cart.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CartItemResponse {
    private UUID id;       // ID của CartItem (để xóa)
    private String productId;
    private String productName;
    private String productImage;
    private BigDecimal price; // Đơn giá
    private int quantity;
    private BigDecimal subTotal; // Thành tiền (price * quantity)
}