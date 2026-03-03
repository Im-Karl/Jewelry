package com.example.jewelry.gamification.web;

import com.example.jewelry.gamification.dto.CreatePublicCouponRequest;
import com.example.jewelry.gamification.dto.PublicCouponResponse;
import com.example.jewelry.shared.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final PublicCouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublicCouponResponse> create(@RequestBody CreatePublicCouponRequest req) {
        return ResponseEntity.ok(couponService.create(req));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PublicCouponResponse>> getAll() {
        return ResponseEntity.ok(couponService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicCouponResponse> getbyID(@PathVariable UUID id) {
        return ResponseEntity.ok(couponService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(@PathVariable UUID id) {
        couponService.delete(id);
        return ResponseEntity.ok(
                new MessageResponse("Xoá mã giảm giá thành công")
        );
    }

}

