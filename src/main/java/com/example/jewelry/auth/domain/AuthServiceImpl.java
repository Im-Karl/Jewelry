package com.example.jewelry.auth.domain;

import com.example.jewelry.auth.dto.*;
import com.example.jewelry.auth.web.AuthService;
import com.example.jewelry.notification.domain.EmailService;
import com.example.jewelry.shared.enums.UserRole;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ModelMapper modelMapper;
    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;

    @Override
    public UserWithTokenDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DomainException(DomainExceptionCode.USER_EXISTED);
        }

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CUSTOMER)
                .loyaltyPoints(0)
                .membershipTier("SILVER")
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtProvider.generateToken(savedUser.getId(), 86400); // 1 ngày

        return new UserWithTokenDto(modelMapper.map(savedUser, UserDto.class), token);
    }

    @Override
    public UserWithTokenDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new DomainException(DomainExceptionCode.USER_BANNED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new DomainException(DomainExceptionCode.WRONG_PASSWORD);
        }

        String token = jwtProvider.generateToken(user.getId(), 86400);

        return new UserWithTokenDto(modelMapper.map(user, UserDto.class), token);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống!"));

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));

        OtpToken otpToken = OtpToken.builder()
                .otpCode(otp)
                .user(user)
                .expiryDate(java.time.LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();

        otpTokenRepository.save(otpToken);

        emailService.sendOtpEmail(email, otp);
    }

    @Transactional
    public void resetPassword(String otpCode, String newPassword) {
        OtpToken token = otpTokenRepository.findByOtpCodeAndIsUsedFalse(otpCode)
                .orElseThrow(() -> new RuntimeException("Mã OTP không hợp lệ hoặc đã được sử dụng!"));

        if (token.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        otpTokenRepository.save(token);
    }
}