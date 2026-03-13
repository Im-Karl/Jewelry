package com.example.jewelry.warranty.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.order.domain.Order;
import com.example.jewelry.order.domain.OrderItem;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import com.example.jewelry.warranty.dto.BookingResponse;
import com.example.jewelry.warranty.dto.CreateBookingRequest;
import com.example.jewelry.warranty.dto.WarrantyResponse;
import com.example.jewelry.warranty.web.WarrantyService; // Import Interface
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarrantyServiceImpl implements WarrantyService { // <--- Implement tại đây

    private final WarrantyRepository warrantyRepository;
    private final ServiceBookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void activateWarrantyForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            Warranty warranty = Warranty.builder()
                    .orderItem(item)
                    .warrantyCode(generateWarrantyCode())
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusYears(1))
                    .status("ACTIVE")
                    .customerPhone(order.getRecipientPhone())
                    .build();
            warrantyRepository.save(warranty);
        }
    }

    @Override
    public List<WarrantyResponse> getAll() {
        List<Warranty> warranties = warrantyRepository.findAll();

        return warranties.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<WarrantyResponse> lookupWarranty(String query) {
        List<Warranty> warranties;
        if (query.length() < 10) {
            warranties = warrantyRepository.findByWarrantyCode(query).stream().toList();
        } else {
            warranties = warrantyRepository.findByCustomerPhone(query);
        }
        return warranties.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse createBooking(UUID userId, CreateBookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));

        Warranty warranty = null;
        if (request.getWarrantyCode() != null) {
            warranty = warrantyRepository.findByWarrantyCode(request.getWarrantyCode())
                    .orElse(null);
        }

        ServiceBooking booking = ServiceBooking.builder()
                .user(user)
                .warranty(warranty)
                .serviceType(request.getServiceType())
                .bookingDate(request.getBookingDate())
                .note(request.getNote())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();


        ServiceBooking saved = bookingRepository.save(booking);

        return mapToResponse(saved);
    }



    private String generateWarrantyCode() {
        return "WB-" + (10000 + new Random().nextInt(90000));
    }

    private WarrantyResponse mapToDto(Warranty w) {
        WarrantyResponse dto = new WarrantyResponse();
        dto.setWarrantyCode(w.getWarrantyCode());
        dto.setStartDate(w.getStartDate());
        dto.setEndDate(w.getEndDate());
        dto.setStatus(w.getStatus());
        dto.setCustomerPhone(w.getCustomerPhone());
        if (w.getOrderItem() != null && w.getOrderItem().getProduct() != null) {
            dto.setProductName(w.getOrderItem().getProduct().getName());
            dto.setProductImage(w.getOrderItem().getProduct().getMainImageUrl());
        }
        return dto;
    }

    private BookingResponse mapToResponse(ServiceBooking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .warrantyCode(
                        b.getWarranty() != null ? b.getWarranty().getWarrantyCode() : null
                )
                .serviceType(b.getServiceType())
                .bookingDate(b.getBookingDate())
                .customerName(b.getUser() != null ? b.getUser().getFullName() : "Khách vãng lai")
                .status(b.getStatus())
                .note(b.getNote())
                .build();
    }
}