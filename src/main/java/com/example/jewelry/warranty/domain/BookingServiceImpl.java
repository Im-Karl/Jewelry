package com.example.jewelry.warranty.domain;

import com.example.jewelry.warranty.dto.BookingResponse;
import com.example.jewelry.warranty.web.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(UUID bookingId, String status) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status.toUpperCase());
        ServiceBooking saveBooking = bookingRepository.save(booking);
        return mapToResponse(saveBooking);
    }

    private BookingResponse mapToResponse(ServiceBooking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .warrantyCode(
                        b.getWarranty() != null
                                ? b.getWarranty().getWarrantyCode()
                                : null
                )
                .customerName(b.getUser() != null ? b.getUser().getFullName() : "Khách vãng lai")
                .serviceType(b.getServiceType())
                .bookingDate(b.getBookingDate())
                .status(b.getStatus())
                .note(b.getNote())
                .build();
    }
}