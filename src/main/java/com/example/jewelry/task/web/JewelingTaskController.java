package com.example.jewelry.task.web;

import com.example.jewelry.shared.enums.JewelingTaskStatus;
import com.example.jewelry.shared.response.PageResponse;
import com.example.jewelry.shared.security.SecurityUtil;
import com.example.jewelry.task.dto.CreateTaskRequest;
import com.example.jewelry.task.dto.JewelingTaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class JewelingTaskController {

    private final JewelingTaskService taskService;

    // SUPPORT hoặc ADMIN tạo việc
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<JewelingTaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        UUID creatorId = SecurityUtil.getCurrentUserId().orElseThrow();
        return ResponseEntity.ok(taskService.createTask(creatorId, request));
    }

    // THỢ KIM HOÀN (JEWELER) cập nhật trạng thái làm việc
    @PatchMapping("/{taskId}/status")
    @PreAuthorize("hasRole('JEWELER')")
    public ResponseEntity<JewelingTaskResponse> updateStatus(
            @PathVariable UUID taskId,
            @RequestParam JewelingTaskStatus status) {
        UUID jewelerId = SecurityUtil.getCurrentUserId().orElseThrow();
        return ResponseEntity.ok(taskService.updateTaskStatus(jewelerId, taskId, status));
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasRole('JEWELER')")
    public ResponseEntity<PageResponse<JewelingTaskResponse>> getMyTasks(
            @RequestParam(required = false) JewelingTaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UUID jewelerId = SecurityUtil.getCurrentUserId().orElseThrow();
        return ResponseEntity.ok(taskService.getMyTasks(jewelerId, status, page, size));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('JEWELER', 'SUPPORT')")
    public ResponseEntity<PageResponse<JewelingTaskResponse>> getAvailableTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(taskService.getAvailableTasks(page, size));
    }
}