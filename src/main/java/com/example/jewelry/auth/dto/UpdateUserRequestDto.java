package com.example.jewelry.auth.dto;

import com.example.jewelry.shared.enums.UserRole;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UpdateUserRequestDto {
    private String fullName;

    private String phoneNumber;

    private MultipartFile avatar;

    private String zodiacSign;

    private LocalDate birthDate;
    private String gender; // male, female, other


    private UserRole role;
}
