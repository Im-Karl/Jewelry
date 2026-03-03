package com.example.jewelry.gamification.web;

import com.example.jewelry.gamification.dto.SpinRequest;
import com.example.jewelry.gamification.dto.SpinResult;

public interface GamificationService {
    SpinResult spinWheel(SpinRequest request);
}