package com.example.jewelry.gamification.web;

import com.example.jewelry.gamification.dto.SpinRequest;
import com.example.jewelry.gamification.dto.SpinResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @PostMapping("/spin")
    public ResponseEntity<SpinResult> spin(@RequestBody SpinRequest request) {
        return ResponseEntity.ok(gamificationService.spinWheel(request));
    }
}