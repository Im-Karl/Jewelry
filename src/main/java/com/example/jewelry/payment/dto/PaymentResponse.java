package com.example.jewelry.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String status;
    private String message;
    private String paymentUrl; // URL để Frontend redirect user tới trang thanh toán
}