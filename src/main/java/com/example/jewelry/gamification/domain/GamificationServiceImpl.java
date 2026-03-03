package com.example.jewelry.gamification.domain;

import com.example.jewelry.gamification.dto.SpinRequest;
import com.example.jewelry.gamification.dto.SpinResult;
import com.example.jewelry.gamification.web.GamificationService;
import com.example.jewelry.notification.domain.EmailService;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GamificationServiceImpl implements GamificationService {

    private final WheelPrizeRepository prizeRepository;
    private final UserCouponRepository couponRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public SpinResult spinWheel(SpinRequest request) {
        // 1. Chặn spam: Mỗi email chỉ được chơi 1 lần
        if (couponRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã tham gia quay thưởng rồi!");
        }

        // 2. Lấy danh sách giải thưởng
        List<WheelPrize> prizes = prizeRepository.findByActiveTrue();
        if (prizes.isEmpty()) {
            throw new RuntimeException("Chương trình quay thưởng đang bảo trì.");
        }

        // 3. Thuật toán chọn ngẫu nhiên theo trọng số (Weighted Random)
        WheelPrize selectedPrize = selectRandomPrize(prizes);

        // 4. Xử lý kết quả
        String code = null;
        if (selectedPrize.isWinning()) {
            // Sinh mã: PREFIX + 4 số cuối của UUID
            code = selectedPrize.getCouponCodePrefix() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

            // Lưu vào DB
            UserCoupon coupon = UserCoupon.builder()
                    .email(request.getEmail())
                    .generatedCode(code)
                    .discountPercent(selectedPrize.getDiscountPercent())
                    .isUsed(false)
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusDays(7)) // Hạn 7 ngày
                    .build();
            couponRepository.save(coupon);

            // Gửi mail
            emailService.sendCouponEmail(request.getEmail(), code, selectedPrize.getDiscountPercent());
        } else {
            // Trường hợp quay vào ô "Chúc may mắn lần sau" => Vẫn lưu email để chặn quay tiếp
            UserCoupon lost = UserCoupon.builder()
                    .email(request.getEmail())
                    .generatedCode("LOSE")
                    .isUsed(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            couponRepository.save(lost);
        }

        // 5. Trả kết quả cho Frontend hiển thị
        return new SpinResult(selectedPrize.getLabel(), selectedPrize.isWinning());
    }

    private WheelPrize selectRandomPrize(List<WheelPrize> prizes) {
        double totalWeight = prizes.stream().mapToDouble(WheelPrize::getProbability).sum();
        double randomValue = new Random().nextDouble() * totalWeight;

        double currentWeight = 0;
        for (WheelPrize prize : prizes) {
            currentWeight += prize.getProbability();
            if (randomValue <= currentWeight) {
                return prize;
            }
        }
        return prizes.get(prizes.size() - 1); // Fallback
    }
}