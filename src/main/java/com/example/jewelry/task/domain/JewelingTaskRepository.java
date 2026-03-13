package com.example.jewelry.task.domain;

import com.example.jewelry.shared.enums.JewelingTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JewelingTaskRepository extends JpaRepository<JewelingTask, UUID> {

    // Dành cho Thợ (JEWELER): Xem các task mình đang làm / đã làm
    Page<JewelingTask> findByJewelerIdAndStatus(UUID jewelerId, JewelingTaskStatus status, Pageable pageable);

    // Dành cho Quản lý / Support: Xem các task chưa ai nhận
    Page<JewelingTask> findByStatus(JewelingTaskStatus status, Pageable pageable);

    Page<JewelingTask> findByJewelerId(UUID jewelerId, Pageable pageable);

    Page<JewelingTask> findByJewelerIsNullAndStatus(JewelingTaskStatus status, Pageable pageable);
}