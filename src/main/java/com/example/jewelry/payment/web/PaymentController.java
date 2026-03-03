package com.example.jewelry.payment.web;

import com.example.jewelry.payment.dto.PaymentRequest;
import com.example.jewelry.payment.dto.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPaymentUrl(request));
    }

    // API 2: Giả lập Callback (Frontend hoặc User click vào link trả về từ API 1)
    // Thực tế: Đây là URL mà VNPAY sẽ gọi ngầm (IPN) hoặc redirect user về
    @GetMapping("/mock-callback")
    public ResponseEntity<PaymentResponse> paymentCallback(HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.handlePaymentCallback(request));
    }
}