package com.example.jewelry.order.dto;

import com.example.jewelry.shared.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}