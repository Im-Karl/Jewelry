package com.example.jewelry.review.web;

import com.example.jewelry.review.dto.CreateReviewRequest;
import com.example.jewelry.review.dto.ReviewResponse;
import com.example.jewelry.shared.response.PageResponse;
import com.example.jewelry.shared.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ReviewResponse> createReview(@ModelAttribute @Valid CreateReviewRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Unauthorized"));
        return ResponseEntity.ok(reviewService.createReview(userId, request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<PageResponse<ReviewResponse>> getProductReviews(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId, page, size));
    }
}