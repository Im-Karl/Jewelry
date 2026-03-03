package com.example.jewelry.auth.domain;

import com.example.jewelry.auth.dto.*;
import com.example.jewelry.auth.web.AuthService;
import com.example.jewelry.shared.enums.UserRole;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ModelMapper modelMapper;

    @Override
    public UserWithTokenDto register(RegisterRequestDto request) {
        // 1. Kiểm tra email trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DomainException(DomainExceptionCode.USER_EXISTED);
        }

        // 2. Tạo User entity từ request
        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CUSTOMER) // Mặc định là khách
                .loyaltyPoints(0)
                .membershipTier("SILVER")
                .build();

        // 3. Lưu xuống DB
        User savedUser = userRepository.save(user);

        // 4. Sinh Token
        String token = jwtProvider.generateToken(savedUser.getId(), 86400); // 1 ngày

        // 5. Trả về DTO
        return new UserWithTokenDto(modelMapper.map(savedUser, UserDto.class), token);
    }

    @Override
    public UserWithTokenDto login(LoginRequestDto request) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new DomainException(DomainExceptionCode.USER_BANNED);
        }

        // 2. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new DomainException(DomainExceptionCode.WRONG_PASSWORD); // Bạn nhớ thêm code này vào enum nhé
        }

        // 3. Sinh Token
        String token = jwtProvider.generateToken(user.getId(), 86400);

        return new UserWithTokenDto(modelMapper.map(user, UserDto.class), token);
    }
}