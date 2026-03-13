package com.example.jewelry.warranty.web;

import com.example.jewelry.warranty.dto.BookingResponse;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    List<BookingResponse> getAll();

    BookingResponse getById(UUID id);

    List<BookingResponse> getByWarrantyCode(String warrantyCode);

    BookingResponse updateBookingStatus(UUID bookingId, String status);
}
