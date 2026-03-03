package com.example.jewelry.warranty.domain;

import com.example.jewelry.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "warranty_id")
    private Warranty warranty;

    private String serviceType; // DANH_BONG, SUA_CHUA, THU_DOI
    private LocalDate bookingDate; // Thời gian khách muốn đến
    private String note; // Mô tả hư hỏng

    // Trạng thái xử lý (Quy trình thợ kim hoàn)
    private String status; // PENDING, CONFIRMED, PROCESSING, COMPLETED, CANCELLED

    private LocalDateTime createdAt;
}