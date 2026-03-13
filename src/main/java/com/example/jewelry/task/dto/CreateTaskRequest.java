package com.example.jewelry.task.dto;

import com.example.jewelry.shared.enums.JewelingTaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateTaskRequest {
    @NotNull(message = "Loại công việc không được để trống")
    private JewelingTaskType taskType;

    @NotNull(message = "ID Tham chiếu (Đơn hàng/Bảo hành) không được để trống")
    private UUID referenceId;

    @NotBlank(message = "Mô tả công việc không được để trống")
    private String description;

    private UUID jewelerId; // (Tùy chọn) Chỉ định đích danh thợ, hoặc để trống cho thợ tự nhận
}