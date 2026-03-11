package com.example.jewelry.review.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.order.domain.OrderRepository;
import com.example.jewelry.product.domain.Product;
import com.example.jewelry.product.domain.ProductRepository;
import com.example.jewelry.review.dto.CreateReviewRequest;
import com.example.jewelry.review.dto.ReviewResponse;
import com.example.jewelry.review.web.ReviewService;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.response.PageResponse;
import com.example.jewelry.shared.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final FileStorageService fileStorageService;


    @Override
    @Transactional
    public ReviewResponse createReview(UUID userId, CreateReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));

        boolean hasPurchased = orderRepository.hasUserPurchasedProduct(userId, request.getProductId());
        if (!hasPurchased) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua hàng");
        }

        if(reviewRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
        }

        String imageUrl = null;
        if(request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = fileStorageService.storeFile(request.getImage());
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .imageUrl(imageUrl)
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToDto(savedReview);
    }

    @Override
    public PageResponse<ReviewResponse> getReviewsByProduct(String productId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);

        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .pageNumber(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .isLast(reviewPage.isLast())
                .build();
    }


    private ReviewResponse mapToDto(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setImageUrl(review.getImageUrl());
        dto.setCreatedAt(review.getCreatedAt());
        if (review.getUser() != null) {
            dto.setUserName(review.getUser().getFullName());
            dto.setUserAvatar(review.getUser().getAvatarUrl());
        }
        return dto;
    }
}
