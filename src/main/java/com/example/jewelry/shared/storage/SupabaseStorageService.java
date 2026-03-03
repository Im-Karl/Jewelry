package com.example.jewelry.shared.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Primary
// @Profile("prod") // Nếu muốn chỉ chạy trên môi trường Production thì dùng dòng này
public class SupabaseStorageService implements FileStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final RestTemplate restTemplate;

    public SupabaseStorageService() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

        String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, fileName);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));

            // Gửi binary data trực tiếp
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // Trả về Public URL để Frontend hiển thị
                // Format: {supabaseUrl}/storage/v1/object/public/{bucket}/{filename}
                return String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, fileName);
            } else {
                throw new RuntimeException("Failed to upload to Supabase: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not upload file to Supabase", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        // Logic xóa: Parse file name từ URL rồi gọi DELETE API của Supabase
        // (Optional: Bạn có thể implement sau nếu cần)
    }
}