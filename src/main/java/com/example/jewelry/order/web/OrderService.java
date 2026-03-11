package com.example.jewelry.order.web;

import com.example.jewelry.order.domain.Order;
import com.example.jewelry.order.dto.CreateOrderRequest;
import com.example.jewelry.order.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID userId, CreateOrderRequest request);
    List<OrderResponse> getMyOrders(UUID userId);
    List<OrderResponse> getAllOrder();
    void updateOrderStatus(UUID orderId, com.example.jewelry.shared.enums.OrderStatus status);

    void deleteMyOrder(UUID userId, UUID orderId);
    void cancelOrder(UUID userId, UUID orderId);
}