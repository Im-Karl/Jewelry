package com.example.jewelry.gamification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WheelPrizeRepository extends JpaRepository<WheelPrize, Long> {
    List<WheelPrize> findByActiveTrue();
}