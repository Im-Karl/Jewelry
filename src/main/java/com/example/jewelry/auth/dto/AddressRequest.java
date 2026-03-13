package com.example.jewelry.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;
    @NotBlank(message = "Số nhà, tên đường không được để trống")
    private String street;
    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;
    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city;
    private boolean isDefault;
}