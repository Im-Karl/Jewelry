package com.example.jewelry.wishlist.web;

import com.example.jewelry.wishlist.domain.Wishlist;
import com.example.jewelry.wishlist.domain.WishlistRepository;
import com.example.jewelry.wishlist.dto.WishlistResponse;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    String toggleWishlist(UUID userId, String productId);
    List<WishlistResponse> getMyWishlists(UUID userId);
}
