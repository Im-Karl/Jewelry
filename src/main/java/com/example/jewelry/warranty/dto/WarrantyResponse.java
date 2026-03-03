package com.example.jewelry.warranty.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WarrantyResponse {
    private String warrantyCode;
    private String productName;
    private String productImage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String customerPhone;
}