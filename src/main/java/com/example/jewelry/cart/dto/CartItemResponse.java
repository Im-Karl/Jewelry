package com.example.jewelry.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private UUID id;       // ID của CartItem (để xóa)
    private String productId;
    private String productName;
    private String productImage;

    private UUID variantId;
    private String size;
    private String color;

    private BigDecimal price; // Đơn giá
    private int quantity;
    private BigDecimal subTotal; // Thành tiền (price * quantity)
}