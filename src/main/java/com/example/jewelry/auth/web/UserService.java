package com.example.jewelry.auth.web;

import com.example.jewelry.auth.dto.UpdateUserRequestDto;
import com.example.jewelry.auth.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto getUserById(UUID id);
    UserDto updateProfile(UUID userId, UpdateUserRequestDto request);

    List<UserDto> getAllUsers();
    void deleteUser(UUID id);

    void updateUserRole(UUID userId, String newRole);
}