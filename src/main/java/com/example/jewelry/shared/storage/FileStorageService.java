package com.example.jewelry.shared.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file); // Trả về đường dẫn file/URL sau khi lưu
    void deleteFile(String fileUrl);      // Xóa file (dùng khi xóa sản phẩm)
}