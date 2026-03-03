package com.example.jewelry.shared.storage;

import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode; // Nhớ thêm mã lỗi FILE_UPLOAD_FAILED vào Enum nha
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path fileStorageLocation;

    public LocalFileStorageService() {
        // Tạo thư mục "uploads" ngay tại thư mục gốc dự án
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // Chuẩn hóa tên file
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Kiểm tra tên file
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Filename contains invalid path sequence " + originalFileName);
            }

            // Tạo tên file mới ngẫu nhiên để tránh trùng (VD: uuid_anh.png)
            String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

            // Copy file vào thư mục đích
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn (Trong thực tế bạn sẽ trả về URL đầy đủ)
            return "/uploads/" + fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        // Logic xóa file (Optional)
    }
}