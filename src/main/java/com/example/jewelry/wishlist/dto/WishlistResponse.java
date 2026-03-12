package com.example.jewelry.wishlist.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WishlistResponse {
    private UUID id;
    private String productId;
    private String productName;
    private BigDecimal basePrice;
    private String mainImageUrl;
    private String categoryName;
    private LocalDateTime addedAt;
}
