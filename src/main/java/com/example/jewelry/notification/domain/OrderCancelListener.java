package com.example.jewelry.notification.domain;

import com.example.jewelry.order.web.OrderService;
import com.example.jewelry.shared.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelListener {

    private final OrderService orderService;

    // Lắng nghe ở Nghĩa trang (DLQ)
    @RabbitListener(queues = RabbitMQConfig.CANCEL_DLQ)
    public void processCancelOrder(String orderIdString) {
        try {
            log.info("[RabbitMQ] - Bắt đầu kiểm tra hủy tự động Order: {}", orderIdString);
            UUID orderId = UUID.fromString(orderIdString);

            // Gọi hàm hủy tự động của hệ thống
            orderService.systemCancelOrder(orderId);

            log.info("[RabbitMQ] - Hoàn tất kiểm tra xử lý Order: {}", orderIdString);
        } catch (Exception e) {
            log.error("Lỗi khi hủy đơn tự động: {}", e.getMessage());
        }
    }
}