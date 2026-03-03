package com.example.jewelry.gamification.dto;


import com.example.jewelry.shared.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpinRequest {
    @NotBlank(message = ErrorMessage.EMAIL_NOT_BLANK)
    private String email;
}
