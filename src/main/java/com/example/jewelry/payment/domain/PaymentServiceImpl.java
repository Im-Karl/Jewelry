package com.example.jewelry.payment.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.order.domain.Order;
import com.example.jewelry.order.domain.OrderRepository;
import com.example.jewelry.order.web.OrderService;
import com.example.jewelry.payment.dto.PaymentRequest;
import com.example.jewelry.payment.dto.PaymentResponse;
import com.example.jewelry.payment.web.PaymentService;
import com.example.jewelry.shared.enums.OrderStatus;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.warranty.domain.WarrantyRepository;
import com.example.jewelry.warranty.web.WarrantyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final WarrantyService warrantyService;


    @Override
    public PaymentResponse createPaymentUrl(PaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.ORDER_NOT_FOUND));

        // 2. Tạo URL giả lập (Trong thực tế đoạn này sẽ gọi VNPAY Library để hash dữ liệu)
        // URL này trỏ về chính server mình, giả vờ như user đã thanh toán thành công
        String fakePaymentUrl = "http://localhost:8080/api/payment/mock-callback?orderId=" + order.getId()
                + "&amount=" + request.getAmount()
                + "&status=SUCCESS";

        return new PaymentResponse("OK", "Successfully generated payment URL", fakePaymentUrl);
    }

    @Override
    @Transactional
    public PaymentResponse handlePaymentCallback(HttpServletRequest request) {
        // 1. Lấy thông tin từ URL callback
        String orderIdStr = request.getParameter("orderId");
        String status = request.getParameter("status");
        String amountStr = request.getParameter("amount");

        if (orderIdStr == null) {
            return new PaymentResponse("FAILED", "Missing orderId", null);
        }

        UUID orderId = UUID.fromString(orderIdStr);

        // 2. Logic xử lý kết quả
        if ("SUCCESS".equals(status)) {
            // Cập nhật trạng thái đơn hàng -> PAID
            orderService.updateOrderStatus(orderId, OrderStatus.PAID);

            // Lưu log giao dịch
            Order order = orderRepository.findById(orderId).orElse(null);

            PaymentTransaction transaction = PaymentTransaction.builder()
                    .order(order)
                    .paymentMethod("MOCK_VNPAY")
                    .transactionCode("TRX_" + System.currentTimeMillis()) // Mã giả
                    .amount(new java.math.BigDecimal(amountStr))
                    .status("SUCCESS")
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(transaction);

            BigDecimal paidAmount = order.getTotalAmount();

            BigDecimal pointsEarnedDecimal = paidAmount.multiply(new BigDecimal("0.08"));

            int pointsEarns = pointsEarnedDecimal.intValue();

            if(pointsEarns > 0){
                User user = order.getUser();
                user.setLoyaltyPoints(user.getLoyaltyPoints() + pointsEarns);

                userRepository.save(user);
            }
            warrantyService.activateWarrantyForOrder(order);

            return new PaymentResponse("SUCCESS", "Payment processed and Points earned", null);
        }

        return new PaymentResponse("FAILED", "Payment failed", null);
    }
}