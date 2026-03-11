package com.example.jewelry.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateReviewRequest {
    @NotBlank(message = "ID Sản phẩm là bắt buộc")
    private String productId;

    @Min(value = 1, message = "Đánh giá thấp nhất là 1 sao")
    @Max(value = 5, message = "Đánh giá cao nhất là 5 sao")
    private int rating;

    @NotBlank(message = " Vui lòng nhập nội dung đánh giá")
    private String comment;

    private MultipartFile image;
}
