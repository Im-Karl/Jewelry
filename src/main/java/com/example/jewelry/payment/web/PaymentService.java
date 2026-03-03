package com.example.jewelry.payment.web;

import com.example.jewelry.payment.dto.PaymentRequest;
import com.example.jewelry.payment.dto.PaymentResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    // Tạo URL thanh toán
    PaymentResponse createPaymentUrl(PaymentRequest request);

    // Xử lý khi cổng thanh toán gọi ngược lại (IPN / Return URL)
    PaymentResponse handlePaymentCallback(HttpServletRequest request);
}