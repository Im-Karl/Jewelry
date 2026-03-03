package com.example.jewelry.warranty.domain;

import com.example.jewelry.warranty.dto.BookingResponse;
import com.example.jewelry.warranty.web.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ServiceBookingRepository bookingRepository;

    @Override
    public List<BookingResponse> getAll() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BookingResponse getById(UUID id) {
        ServiceBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getByWarrantyCode(String warrantyCode) {
        return bookingRepository
                .findByWarranty_WarrantyCode(warrantyCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BookingResponse mapToResponse(ServiceBooking b) {
        return BookingResponse.builder()
                .warrantyCode(
                        b.getWarranty() != null
                                ? b.getWarranty().getWarrantyCode()
                                : null
                )
                .serviceType(b.getServiceType())
                .bookingDate(b.getBookingDate())
                .note(b.getNote())
                .build();
    }
}