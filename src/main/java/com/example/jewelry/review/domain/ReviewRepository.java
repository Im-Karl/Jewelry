package com.example.jewelry.review.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByProductId(String productId, Pageable pageable);

    boolean existsByUserIdAndProductId(UUID userId, String productId);
}
