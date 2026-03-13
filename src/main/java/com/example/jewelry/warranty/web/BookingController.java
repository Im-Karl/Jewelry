package com.example.jewelry.warranty.web;

import com.example.jewelry.warranty.dto.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<List<BookingResponse>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) { // "APPROVED","REJECTED"
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @GetMapping("/code/{warrantyCode}")
    public ResponseEntity<List<BookingResponse>> getByWarrantyCode(
            @PathVariable String warrantyCode) {

        return ResponseEntity.ok(
                bookingService.getByWarrantyCode(warrantyCode)
        );
    }
}


