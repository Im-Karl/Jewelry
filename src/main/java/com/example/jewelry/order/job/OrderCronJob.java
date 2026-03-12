package com.example.jewelry.order.job;

import com.example.jewelry.order.domain.Order;
import com.example.jewelry.order.domain.OrderRepository;
import com.example.jewelry.shared.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCronJob {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void autoCompleteShippingOrders() {
        log.info("Bắt đầu chạy Cronjob: Tự động hoàn thành đơn hàng đang giao...");

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(7);

        List<Order> shippingOrders = orderRepository.findByStatusAndUpdatedAtBefore(OrderStatus.SHIPPING, threeDaysAgo);

        int count = 0;
        for (Order order : shippingOrders) {
            order.setStatus(OrderStatus.COMPLETED);
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);
            count++;

        }

        log.info("Đã tự động chuyển {} đơn hàng sang trạng thái COMPLETED.", count);
    }
}