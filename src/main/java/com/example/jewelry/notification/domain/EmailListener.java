package com.example.jewelry.notification.domain;

import com.example.jewelry.order.domain.Order;
import com.example.jewelry.order.domain.OrderRepository;
import com.example.jewelry.shared.config.RabbitMQConfig;
import com.example.jewelry.shared.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;
    private final OrderRepository orderRepository;

    // Lắng nghe liên tục ở cái Queue này
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    @Transactional
    public void processEmailTask(String orderIdString) {
        try {
            log.info("Bắt đầu xử lý gửi email cho Order ID: {}", orderIdString);
            UUID orderId = UUID.fromString(orderIdString);

            // Tìm lại Order từ DB
            Order order = orderRepository.findById(orderId).orElse(null);

            if (order != null) {
                int loadItems = order.getItems().size(); // Ép load Lazy

                // 👉 KIỂM TRA TRẠNG THÁI ĐỂ GỬI ĐÚNG LOẠI EMAIL
                if (order.getStatus() == OrderStatus.CANCELLED) {
                    // GỬI EMAIL #3: THÔNG BÁO HỦY ĐƠN (Hết TTL)
                    // Lưu ý: Bạn cần tạo thêm hàm sendOrderCancelledEmail() trong EmailService nhé!
                    emailService.sendOrderCancelledEmail(order);
                    log.info("Đã gửi email HỦY ĐƠN cho Order ID: {}", orderId);

                } else {
                    // GỬI EMAIL #1 hoặc #2: XÁC NHẬN ĐƠN HÀNG (Dùng chung 1 template cũng được)
                    emailService.sendOrderConfirmation(order);
                    log.info("Đã gửi email XÁC NHẬN cho Order ID: {}", orderId);
                }
            } else {
                log.warn("KHÔNG TÌM THẤY ORDER {} TRONG DATABASE!", orderId);
            }
        } catch (Exception e) {
            log.error("Lỗi khi gửi email cho Order {}: {}", orderIdString, e.getMessage());
            // Tương lai có thể cấu hình retry (thử lại) nếu gửi mail bị lỗi
            e.printStackTrace();
        }
    }
}