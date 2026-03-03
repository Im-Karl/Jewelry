package com.example.jewelry.shared.security;

import com.example.jewelry.auth.dto.UserDto;
import com.example.jewelry.shared.enums.UserRole;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class SecurityUtil {
    public static Optional<UUID> getCurrentUserId() {
        return getAuthenticatedUser().map(UserDto::getId);
    }

    public static Optional<UserRole> getCurrentUserRole() {
        return getAuthenticatedUser().map(UserDto::getRole);
    }

//    public static Optional<String> getCurrentUserStoreCode() {
//        return getAuthenticatedUser().map(UserDto::getStaffOfStoreCode);
//    }

    public static void onlyAllowUserId(UUID userId) {
        UUID currentId = getCurrentUserId().orElse(null);
        if (currentId == null || !currentId.equals(userId)) {
            throw new DomainException(DomainExceptionCode.ACCESS_DENIED);
        }
    }

    public static void onlyAllowRoles(UserRole... allowedRoles) {
        UserRole currentRole = getCurrentUserRole().orElse(null);
        if (currentRole == null || !Arrays.asList(allowedRoles).contains(currentRole)) {
            throw new DomainException(DomainExceptionCode.ACCESS_DENIED);
        }
    }

//    public static void onlyAllowStaffOfStoreCode(String storeCode) {
//        String currentStoreCode = getCurrentUserStoreCode().orElse(null);
//        if (currentStoreCode == null || !currentStoreCode.equals(storeCode)) {
//            throw new DomainException(DomainExceptionCode.ACCESS_DENIED);
//        }
//    }

    private static Optional<UserDto> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDto authUser) {
            return Optional.of(authUser);
        }

        return Optional.empty();
    }
}
