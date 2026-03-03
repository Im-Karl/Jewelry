package com.example.jewelry.auth.web;

import com.example.jewelry.auth.dto.LoginRequestDto;
import com.example.jewelry.auth.dto.RegisterRequestDto;
import com.example.jewelry.auth.dto.UserWithTokenDto;

public interface AuthService {
    UserWithTokenDto register(RegisterRequestDto request);
    UserWithTokenDto login(LoginRequestDto request);
}