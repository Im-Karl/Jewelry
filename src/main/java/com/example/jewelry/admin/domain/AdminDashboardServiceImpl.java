package com.example.jewelry.admin.domain;

import com.example.jewelry.admin.dto.DashboardStatisticsResponse;
import com.example.jewelry.admin.web.AdminDashboardService;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardStatisticsResponse getGeneralStatistics() {
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        if(totalRevenue == null){
            totalRevenue = BigDecimal.ZERO;
        }

        long totalOrders = orderRepository.count();

        long totalUsers = userRepository.count();

        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long newOrdersToday = orderRepository.countOrdersFrom(startOfToday);

        return DashboardStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalUsers(totalUsers)
                .newOrdersToday(newOrdersToday)
                .build();
    }
}
