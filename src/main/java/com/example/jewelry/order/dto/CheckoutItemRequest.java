package com.example.jewelry.order.dto;

import com.example.jewelry.shared.constants.ErrorMessage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutItemRequest {
    @NotBlank(message = "Product ID không được để trống")
    private String productId;

    @Min(value = 1, message = ErrorMessage.ITEM_MIN_LENGTH)
    private int quantity;
}