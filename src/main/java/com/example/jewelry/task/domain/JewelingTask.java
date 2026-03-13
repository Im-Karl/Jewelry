package com.example.jewelry.task.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.shared.enums.JewelingTaskStatus;
import com.example.jewelry.shared.enums.JewelingTaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jeweling_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JewelingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Phân loại task: Sửa chữa bảo hành hay Đơn hàng mới
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JewelingTaskType taskType;

    // ID tham chiếu (Có thể là ID của OrderItem hoặc WarrantyBooking)
    @Column(nullable = false)
    private UUID referenceId;

    // Ghi chú công việc chi tiết từ SUPPORT (VD: "Nong nhẫn từ size 6 lên size 7, khắc chữ T&M")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // Người thợ kim hoàn được giao việc (Có thể null lúc đầu nếu chưa ai nhận)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jeweler_id")
    private User jeweler;

    // Ai là người tạo task này (Thường là SUPPORT hoặc ADMIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JewelingTaskStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = JewelingTaskStatus.TODO;
        }
    }
}