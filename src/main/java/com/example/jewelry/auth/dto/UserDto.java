package com.example.jewelry.auth.dto;

import com.example.jewelry.shared.constants.ErrorMessage;
import com.example.jewelry.shared.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;

    @NotBlank(message = ErrorMessage.EMAIL_NOT_BLANK)
    private String email;

    @NotBlank(message = ErrorMessage.BLANK_NAME)
    private String fullName;

    private UserRole role;

    private String avatarUrl;
}