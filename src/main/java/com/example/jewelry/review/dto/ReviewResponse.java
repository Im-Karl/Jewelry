package com.example.jewelry.review.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewResponse {
    private UUID id;
    private String userName;
    private String userAvatar;
    private int rating;
    private String comment;
    private String imageUrl;
    private LocalDateTime createdAt;

}
