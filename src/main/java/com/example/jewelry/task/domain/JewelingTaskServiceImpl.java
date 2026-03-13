package com.example.jewelry.task.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.shared.enums.JewelingTaskStatus;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.response.PageResponse;
import com.example.jewelry.task.dto.CreateTaskRequest;
import com.example.jewelry.task.dto.JewelingTaskResponse;
import com.example.jewelry.task.web.JewelingTaskService;
// ... (Các import khác như Page, PageResponse) ...
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JewelingTaskServiceImpl implements JewelingTaskService {

    private final JewelingTaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public JewelingTaskResponse createTask(UUID creatorId, CreateTaskRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        JewelingTask.JewelingTaskBuilder taskBuilder = JewelingTask.builder()
                .taskType(request.getTaskType())
                .referenceId(request.getReferenceId())
                .description(request.getDescription())
                .assignedBy(creator)
                .status(JewelingTaskStatus.TODO);

        // Nếu Support chỉ định đích danh thợ kim hoàn
        if (request.getJewelerId() != null) {
            User jeweler = userRepository.findById(request.getJewelerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thợ kim hoàn"));
            // (Optional: Có thể check thêm xem user này có role JEWELER không)
            taskBuilder.jeweler(jeweler);
        }

        JewelingTask savedTask = taskRepository.save(taskBuilder.build());
        return mapToDto(savedTask); // Bạn tự viết hàm mapToDto nhé
    }

    @Override
    @Transactional
    public JewelingTaskResponse updateTaskStatus(UUID jewelerId, UUID taskId, JewelingTaskStatus newStatus) {
        JewelingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Task"));

        // Nếu task chưa có ai nhận, và thợ update status thành IN_PROGRESS -> Gán task này cho thợ đó luôn
        if (task.getJeweler() == null && newStatus == JewelingTaskStatus.IN_PROGRESS) {
            User jeweler = userRepository.findById(jewelerId)
                    .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
            task.setJeweler(jeweler);
        }
        // Nếu task đã có người nhận, phải đảm bảo đúng người đó mới được update
        else if (task.getJeweler() != null && !task.getJeweler().getId().equals(jewelerId)) {
            throw new RuntimeException("Bạn không có quyền cập nhật Task của thợ khác!");
        }

        task.setStatus(newStatus);

        // Ghi nhận thời gian hoàn thành
        if (newStatus == JewelingTaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
            // Tương lai: Gọi một Event (Kafka/RabbitMQ) hoặc Service khác để báo cho SUPPORT biết đồ đã sửa xong
        }

        return mapToDto(taskRepository.save(task));
    }

    @Override
    public PageResponse<JewelingTaskResponse> getMyTasks(UUID jewelerId, JewelingTaskStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<JewelingTask> taskPage;

        // Nếu có truyền status (VD: Chỉ lấy task TODO) thì lọc, không thì lấy tất cả
        if (status != null) {
            taskPage = taskRepository.findByJewelerIdAndStatus(jewelerId, status, pageable);
        } else {
            taskPage = taskRepository.findByJewelerId(jewelerId, pageable);
        }

        List<JewelingTaskResponse> content = taskPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return PageResponse.<JewelingTaskResponse>builder()
                .content(content)
                .pageNumber(taskPage.getNumber())
                .pageSize(taskPage.getSize())
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .isLast(taskPage.isLast())
                .build();
    }

    @Override
    public PageResponse<JewelingTaskResponse> getAvailableTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        Page<JewelingTask> taskPage = taskRepository.findByJewelerIsNullAndStatus(JewelingTaskStatus.TODO, pageable);

        List<JewelingTaskResponse> content = taskPage.stream().map(this::mapToDto).collect(Collectors.toList());

        return PageResponse.<JewelingTaskResponse>builder()
                .content(content)
                .pageNumber(taskPage.getNumber())
                .pageSize(taskPage.getSize())
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .isLast(taskPage.isLast())
                .build();
    }

    // Hàm chuyển đổi Entity -> DTO (Mảnh ghép còn thiếu)
    private JewelingTaskResponse mapToDto(JewelingTask task) {
        JewelingTaskResponse.JewelingTaskResponseBuilder builder = JewelingTaskResponse.builder()
                .id(task.getId())
                .taskType(task.getTaskType())
                .referenceId(task.getReferenceId())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .completedAt(task.getCompletedAt());

        // Nếu đã có thợ nhận việc thì map thông tin thợ
        if (task.getJeweler() != null) {
            builder.jewelerId(task.getJeweler().getId())
                    .jewelerName(task.getJeweler().getFullName()); // Giả sử entity User có hàm getFullName()
        }

        // Map thông tin người giao việc
        if (task.getAssignedBy() != null) {
            builder.assignedById(task.getAssignedBy().getId())
                    .assignedByName(task.getAssignedBy().getFullName());
        }

        return builder.build();
    }
}