package com.example.jewelry.cart.web;

import com.example.jewelry.cart.dto.AddToCartRequest;
import com.example.jewelry.cart.dto.CartResponse; // Import DTO mới
import java.util.UUID;

public interface CartService {
    CartResponse getMyCart(UUID userId);
    CartResponse addToCart(UUID userId, AddToCartRequest request);
    CartResponse removeFromCart(UUID userId, UUID cartItemId);
    CartResponse updateQuantity(UUID userId, UUID cartItemId, int quantity);
    void clearCart(UUID userId);

}