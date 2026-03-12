package com.example.jewelry.wishlist.web;

import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.response.MessageResponse;
import com.example.jewelry.shared.security.SecurityUtil;
import com.example.jewelry.wishlist.domain.Wishlist;
import com.example.jewelry.wishlist.domain.WishlistRepository;
import com.example.jewelry.wishlist.dto.WishlistResponse;
import io.jsonwebtoken.security.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/{productId}")
    public ResponseEntity<MessageResponse> toggleWishlist(@PathVariable String productId){
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.ACCESS_DENIED));

        String resultMessage = wishlistService.toggleWishlist(userId, productId);
        return ResponseEntity.ok(new MessageResponse(resultMessage));
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getAllWishlists(){
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.ACCESS_DENIED));

        return ResponseEntity.ok(wishlistService.getMyWishlists(userId));
    }
}
