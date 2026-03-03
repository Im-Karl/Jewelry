package com.example.jewelry.warranty.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateBookingRequest {
    private String warrantyCode; // Có thể null nếu khách muốn sửa dịch vụ ngoài
    private String serviceType;  // POLISH (Đánh bóng), REPAIR (Sửa chữa)
    private LocalDate bookingDate;
    private String note;
}