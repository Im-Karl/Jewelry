package com.example.jewelry.auth.dto;

import com.example.jewelry.shared.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = ErrorMessage.EMAIL_NOT_BLANK)
    private String email;

    @NotBlank(message = ErrorMessage.BLANK_PASSWORD)
    private String password;
}