package com.example.jewelry.wishlist.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository  extends JpaRepository<Wishlist, UUID> {
    Optional<Wishlist> findByUserIdAndProductId(UUID userId, String productId);

    List<Wishlist> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
