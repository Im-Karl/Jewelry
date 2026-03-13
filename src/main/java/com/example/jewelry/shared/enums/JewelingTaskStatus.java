package com.example.jewelry.shared.enums;

public enum JewelingTaskStatus {
    TODO,           // Chờ tiếp nhận (Support vừa tạo)
    IN_PROGRESS,    // Đang gia công (Thợ đang làm)
    COMPLETED,      // Đã xong (Thợ làm xong)
    CANCELLED       // Hủy (Khách đổi ý không sửa nữa)
}