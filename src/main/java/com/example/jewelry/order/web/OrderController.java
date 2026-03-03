package com.example.jewelry.order.web;

import com.example.jewelry.order.dto.CreateOrderRequest;
import com.example.jewelry.order.dto.OrderResponse;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    public record MessageResponse(String message) {}
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<MessageResponse> deleteMyOrder(@PathVariable UUID orderId) {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        orderService.deleteMyOrder(userId, orderId);

        return ResponseEntity.ok(
                new MessageResponse("Xóa đơn hàng thành công")
        );
    }
}