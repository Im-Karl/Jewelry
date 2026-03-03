package com.example.jewelry.gamification.web;

import com.example.jewelry.gamification.dto.AvailableCouponResponse;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final PublicCouponService publicCouponService;

    @GetMapping("/available")
    public ResponseEntity<List<AvailableCouponResponse>> getMyCoupons() {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        return ResponseEntity.ok(publicCouponService.getAvailableCouponsForUser(userId));
    }
}