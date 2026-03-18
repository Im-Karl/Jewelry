package com.example.jewelry.gamification.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.gamification.dto.AvailableCouponResponse;
import com.example.jewelry.gamification.dto.CreatePublicCouponRequest;
import com.example.jewelry.gamification.dto.PublicCouponResponse;
import com.example.jewelry.gamification.web.PublicCouponService;
import com.example.jewelry.order.domain.Order;
import com.example.jewelry.order.domain.OrderItem;
import com.example.jewelry.order.domain.OrderRepository;
import com.example.jewelry.product.domain.ProductRepository;
import com.example.jewelry.product.domain.ProductVariant;
import com.example.jewelry.product.domain.ProductVariantRepository;
import com.example.jewelry.shared.enums.OrderStatus;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicCouponServiceImpl implements PublicCouponService {

    private final PublicCouponRepository publicCouponRepository;
    private final UserCouponRepository userCouponRepository; // Inject thêm cái này
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    @Transactional
    public PublicCouponResponse create(CreatePublicCouponRequest request) {

        if (publicCouponRepository.existsByCode(request.getCode())) {
            throw new DomainException(DomainExceptionCode.COUPON_CODE_EXISTED);
        }

        PublicCoupon coupon = PublicCoupon.builder()
                .code(request.getCode().toUpperCase())
                .discountAmount(request.getDiscountAmount())
                .maxUsage(request.getMaxUsage())
                .usedCount(0)
                .startAt(LocalDateTime.now())
                .expiredAt(request.getExpiredAt())
                .active(true)
                .build();

        PublicCoupon saved = publicCouponRepository.save(coupon);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!publicCouponRepository.existsById(id)) {
            throw new DomainException(DomainExceptionCode.COUPON_NOT_FOUND);
        }
        publicCouponRepository.deleteById(id);
    }

    @Override
    public List<PublicCouponResponse> getAll() {
        return publicCouponRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PublicCouponResponse getById(UUID id) {
        PublicCoupon coupon = publicCouponRepository.findById(id)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.COUPON_NOT_FOUND));
        return toResponse(coupon);
    }

    @Override
    public List<AvailableCouponResponse> getAvailableCouponsForUser(UUID userId) {
        List<AvailableCouponResponse> result = new ArrayList<>();

        // 1. Lấy thông tin User để biết Email
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        // 2. Lấy Public Coupons (Mã chung)
        List<PublicCoupon> publicCoupons = publicCouponRepository.findAvailablePublicCoupons();
        for (PublicCoupon pc : publicCoupons) {
            result.add(AvailableCouponResponse.builder()
                    .code(pc.getCode())
                    .discountAmount(pc.getDiscountAmount())
                    .type("PUBLIC") // Nhãn
                    .expiredAt(pc.getExpiredAt())
                    .description("Mã giảm giá toàn sàn")
                    .build());
        }

        // 3. Lấy Private Coupons (Mã riêng theo Email)
        List<UserCoupon> userCoupons = userCouponRepository.findValidCouponsByEmail(user.getEmail());
        for (UserCoupon uc : userCoupons) {
            result.add(AvailableCouponResponse.builder()
                    .code(uc.getGeneratedCode())
                    .discountAmount(uc.getDiscountAmount())
                    .type("PERSONAL") // Nhãn
                    .expiredAt(uc.getExpiredAt())
                    .description("Mã trúng thưởng của riêng bạn")
                    .build());
        }

        // Có thể sort theo % giảm giá cao nhất lên đầu
        result.sort((c1, c2) -> c2.getDiscountAmount().compareTo(c1.getDiscountAmount()));
        return result;
    }


    private PublicCouponResponse toResponse(PublicCoupon coupon) {
        return PublicCouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountAmount(coupon.getDiscountAmount())
                .maxUsage(coupon.getMaxUsage())
                .usedCount(coupon.getUsedCount())
                .startAt(coupon.getStartAt())
                .expiredAt(coupon.getExpiredAt())
                .active(coupon.isActive())
                .build();
    }
}


