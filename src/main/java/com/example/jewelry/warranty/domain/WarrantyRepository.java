package com.example.jewelry.warranty.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarrantyRepository extends JpaRepository<Warranty, UUID> {
    // Tra cứu bằng Mã bảo hành hoặc SĐT
    Optional<Warranty> findByWarrantyCode(String code);

    @Query("SELECT w FROM Warranty w WHERE w.customerPhone = :phone")
    List<Warranty> findByCustomerPhone(String phone);
}