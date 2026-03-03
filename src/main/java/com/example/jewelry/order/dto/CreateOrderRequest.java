package com.example.jewelry.order.dto;

import com.example.jewelry.shared.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "Địa chỉ giao hàng là bắt buộc")
    private String shippingAddress;

    @NotBlank(message = "Tên người nhận là bắt buộc")
    private String recipientName;

    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    private String recipientPhone;

    private PaymentMethod paymentMethod;
    private boolean isGift;
    private String giftMessage;

    private String couponCode;

    @Min(value = 0, message = "Điểm sử dụng không được là số âm")
    private int pointsToUse = 0;

    // --- THAY ĐỔI QUAN TRỌNG ---
    // Danh sách các ID của CartItem mà user muốn mua (VD: User tích 3 món trong giỏ)
    @NotEmpty(message = "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán")
    private List<UUID> cartItemIds;
}