package com.example.jewelry.task.dto;

import com.example.jewelry.shared.enums.JewelingTaskStatus;
import com.example.jewelry.shared.enums.JewelingTaskType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class JewelingTaskResponse {
    private UUID id;
    private JewelingTaskType taskType;
    private UUID referenceId;
    private String description;

    // Thông tin thợ kim hoàn (Có thể null nếu chưa ai nhận việc)
    private UUID jewelerId;
    private String jewelerName;

    // Thông tin người giao việc (Support/Admin)
    private UUID assignedById;
    private String assignedByName;

    private JewelingTaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}