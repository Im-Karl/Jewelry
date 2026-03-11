package com.example.jewelry.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddToCartRequest {
    @NotNull(message = "Sản phẩm không được để trống")
    private String productId;

    @NotNull(message = "Vui lòng chọn phân loại hàng (Size/Màu)")
    private UUID variantId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;
}