package com.example.jewelry.cart.web;

import com.example.jewelry.cart.dto.AddToCartRequest;
import com.example.jewelry.cart.dto.CartResponse;
import com.example.jewelry.cart.dto.UpdateCartItemQuantityRequest;
import com.example.jewelry.order.web.OrderController;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    public record MessageResponse(String message) {}

    private final CartService cartService;

    @GetMapping("/")
    public ResponseEntity<CartResponse> getMyCart() {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return ResponseEntity.ok(cartService.getMyCart(userId));
    }

    // Thêm vào giỏ
    @PostMapping("/")
    public ResponseEntity<CartResponse> addToCart(@RequestBody @Valid AddToCartRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    // Xóa 1 món (VD: bấm dấu X)
    @DeleteMapping("/{itemId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable UUID itemId) {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return ResponseEntity.ok(cartService.removeFromCart(userId, itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearCart() {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        cartService.clearCart(userId);
        return ResponseEntity.ok(
                new MessageResponse("Dọn dẹp giỏ hàng thành công")
        );
    }


    @PutMapping("/{itemId}/quantity")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable UUID itemId,
            @RequestBody @Valid UpdateCartItemQuantityRequest request
    ) {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        return ResponseEntity.ok(
                cartService.updateQuantity(userId, itemId, request.getQuantity())
        );
    }
}