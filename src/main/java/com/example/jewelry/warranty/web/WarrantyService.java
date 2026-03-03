package com.example.jewelry.warranty.web;

import com.example.jewelry.order.domain.Order;
import com.example.jewelry.warranty.domain.ServiceBooking;
import com.example.jewelry.warranty.dto.BookingResponse;
import com.example.jewelry.warranty.dto.CreateBookingRequest;
import com.example.jewelry.warranty.dto.WarrantyResponse;

import java.util.List;
import java.util.UUID;

public interface WarrantyService {
    // Kích hoạt bảo hành (Dùng nội bộ khi đơn hoàn thành)
    void activateWarrantyForOrder(Order order);

    List<WarrantyResponse> getAll();
    // Tra cứu cho khách (Public)
    List<WarrantyResponse> lookupWarranty(String query);

    // Đặt lịch (User)
    BookingResponse createBooking(UUID userId, CreateBookingRequest request);
}