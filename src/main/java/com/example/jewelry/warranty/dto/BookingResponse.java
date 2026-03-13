package com.example.jewelry.warranty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private UUID id;
    private String warrantyCode; // Có thể null nếu khách muốn sửa dịch vụ ngoài
    private String customerName;
    private String serviceType;  // POLISH (Đánh bóng), REPAIR (Sửa chữa)
    private String status;
    private LocalDate bookingDate;
    private String note;
    private LocalDateTime createdAt;
}
