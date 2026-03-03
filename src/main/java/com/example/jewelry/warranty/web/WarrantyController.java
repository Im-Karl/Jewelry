package com.example.jewelry.warranty.web;

import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.shared.security.SecurityUtil;
import com.example.jewelry.warranty.domain.ServiceBooking;
import com.example.jewelry.warranty.domain.WarrantyRepository;
import com.example.jewelry.warranty.dto.BookingResponse;
import com.example.jewelry.warranty.dto.CreateBookingRequest;
import com.example.jewelry.warranty.dto.WarrantyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warranties")
@RequiredArgsConstructor
public class WarrantyController {

    private final WarrantyService warrantyService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WarrantyResponse> > getAll() {
        return ResponseEntity.ok(warrantyService.getAll());
    }

    @GetMapping("/lookup")
    public ResponseEntity<List<WarrantyResponse>> lookup(@RequestParam String query) {
        return ResponseEntity.ok(warrantyService.lookupWarranty(query));
    }

    @PostMapping("/booking")
    public ResponseEntity<BookingResponse> bookService(@RequestBody CreateBookingRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        return ResponseEntity.ok(warrantyService.createBooking(userId, request));
    }
}