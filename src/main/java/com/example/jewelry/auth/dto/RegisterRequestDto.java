package com.example.jewelry.auth.dto;

import com.example.jewelry.shared.constants.ErrorMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = ErrorMessage.EMAIL_NOT_BLANK)
    @Email(message = ErrorMessage.EMAIL_INVALID)
    private String email;

    @NotBlank(message = ErrorMessage.BLANK_PASSWORD)
    @Size(min = 6, message =  ErrorMessage.PASSWORD_MIN_LENGTH)
    private String password;

    @NotBlank(message = ErrorMessage.BLANK_NAME)
    private String fullName;
}