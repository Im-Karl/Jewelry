package com.example.jewelry.auth.web;

import com.example.jewelry.auth.dto.UpdateUserRequestDto;
import com.example.jewelry.auth.dto.UserDto;
import com.example.jewelry.shared.security.SecurityUtil;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile() {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PatchMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<UserDto> updateProfile(@ModelAttribute @Valid UpdateUserRequestDto request) {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


}