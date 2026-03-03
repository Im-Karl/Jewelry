package com.example.jewelry.gamification.web;


import com.example.jewelry.gamification.domain.PublicCoupon;
import com.example.jewelry.gamification.dto.AvailableCouponResponse;
import com.example.jewelry.gamification.dto.CreatePublicCouponRequest;
import com.example.jewelry.gamification.dto.PublicCouponResponse;

import java.util.List;
import java.util.UUID;

public interface PublicCouponService {

    PublicCouponResponse create(CreatePublicCouponRequest request);

    void delete(UUID id);

    List<PublicCouponResponse> getAll();

    PublicCouponResponse getById(UUID id);
    List<AvailableCouponResponse> getAvailableCouponsForUser(UUID userId);
}
