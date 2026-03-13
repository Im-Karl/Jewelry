package com.example.jewelry.task.web;

import com.example.jewelry.task.dto.CreateTaskRequest;
import com.example.jewelry.task.dto.JewelingTaskResponse;
import com.example.jewelry.shared.enums.JewelingTaskStatus;
import com.example.jewelry.shared.response.PageResponse;
import java.util.UUID;

public interface JewelingTaskService {
    // Support/Admin tạo task
    JewelingTaskResponse createTask(UUID creatorId, CreateTaskRequest request);

    // Thợ Kim Hoàn cập nhật trạng thái (Bắt đầu làm / Đã xong)
    JewelingTaskResponse updateTaskStatus(UUID jewelerId, UUID taskId, JewelingTaskStatus newStatus);

    // Lấy danh sách việc của 1 Thợ
    PageResponse<JewelingTaskResponse> getMyTasks(UUID jewelerId, JewelingTaskStatus status, int page, int size);

    // Lấy danh sách trống
    PageResponse<JewelingTaskResponse> getAvailableTasks(int page, int size);
}