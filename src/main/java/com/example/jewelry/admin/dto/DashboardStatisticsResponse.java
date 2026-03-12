package com.example.jewelry.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatisticsResponse {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalUsers;
    private long newOrdersToday;
}


