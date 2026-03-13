package com.example.jewelry.auth.web;

import com.example.jewelry.auth.dto.LoginRequestDto;
import com.example.jewelry.auth.dto.RegisterRequestDto;
import com.example.jewelry.auth.dto.UserWithTokenDto;
import com.example.jewelry.shared.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserWithTokenDto> register(@RequestBody @Valid RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserWithTokenDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestParam String email) {
        // Có thể bạn viết logic trong UserService hoặc AuthService đều được
        authService.forgotPassword(email);
        return ResponseEntity.ok(new MessageResponse("Mã OTP đã được gửi. Kiểm tra log Console!"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestParam String otp,
            @RequestParam String newPassword) {
        authService.resetPassword(otp, newPassword);
        return ResponseEntity.ok(new MessageResponse("Đổi mật khẩu thành công! Vui lòng đăng nhập lại."));
    }
}