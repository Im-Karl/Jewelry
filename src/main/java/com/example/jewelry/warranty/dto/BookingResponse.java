package com.example.jewelry.warranty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private String warrantyCode; // Có thể null nếu khách muốn sửa dịch vụ ngoài
    private String serviceType;  // POLISH (Đánh bóng), REPAIR (Sửa chữa)
    private LocalDate bookingDate;
    private String note;
}
