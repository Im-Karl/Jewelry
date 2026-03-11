package com.example.jewelry.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private UUID id;
    private BigDecimal totalAmount; // Tổng tiền cả giỏ
    private List<CartItemResponse> items;
}