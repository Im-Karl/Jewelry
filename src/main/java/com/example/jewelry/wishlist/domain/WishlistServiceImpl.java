package com.example.jewelry.wishlist.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.product.domain.Product;
import com.example.jewelry.product.domain.ProductRepository;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.wishlist.dto.WishlistResponse;
import com.example.jewelry.wishlist.web.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public String toggleWishlist(UUID userId, String productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));

        Optional<Wishlist> existingWishlist = wishlistRepository.findByUserIdAndProductId(userId, productId);
        if (existingWishlist.isPresent()) {
            wishlistRepository.delete(existingWishlist.get());
            return "Đã bỏ yêu thích sản phẩm";
        }else{
            Wishlist wishlist =  Wishlist.builder()
                    .user(user)
                    .product(product)
                    .build();
            wishlistRepository.save(wishlist);
            return  "Đã thêm sản phẩm vào yêu thích";
        }
    }

    @Override
    public List<WishlistResponse> getMyWishlists(UUID userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return wishlists.stream().map(w -> WishlistResponse.builder()
                .id(w.getId())
                .productId(w.getProduct().getId())
                .productName(w.getProduct().getName())
                .basePrice(w.getProduct().getBasePrice())
                .mainImageUrl(w.getProduct().getMainImageUrl())
                .categoryName(w.getProduct().getCategory() != null ? w.getProduct().getCategory().getName() : null)
                .addedAt(w.getCreatedAt())
                .build()
        ).collect(Collectors.toList());
    }

}
