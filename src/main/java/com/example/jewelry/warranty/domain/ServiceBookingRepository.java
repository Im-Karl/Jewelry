package com.example.jewelry.warranty.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {
    List<ServiceBooking> findByUserId(UUID userId);

    List<ServiceBooking> findByWarranty_WarrantyCode(String warrantyCode);
}