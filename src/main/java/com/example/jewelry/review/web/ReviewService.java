package com.example.jewelry.review.web;

import com.example.jewelry.review.domain.Review;
import com.example.jewelry.review.dto.CreateReviewRequest;
import com.example.jewelry.review.dto.ReviewResponse;
import com.example.jewelry.shared.response.PageResponse;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(UUID userId, CreateReviewRequest request);
    PageResponse<ReviewResponse> getReviewsByProduct(String productId, int page, int size);

}
