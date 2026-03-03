package com.example.jewelry.consultation.web;

import com.example.jewelry.consultation.dto.ConsultationResult;
import com.example.jewelry.consultation.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @GetMapping("/feng-shui")
    public ResponseEntity<ConsultationResult> getFengShuiAdvice(
            @RequestParam int day,
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(
                consultationService.getConsultation(day, month, year)
        );
    }
}