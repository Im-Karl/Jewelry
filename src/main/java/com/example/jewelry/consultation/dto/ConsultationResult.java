package com.example.jewelry.consultation.dto;

import com.example.jewelry.product.dto.ProductDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ConsultationResult {
    private String element;      // Mệnh (Kim, Mộc...)
    private String zodiac;
    private String description;  // Lời khuyên (Màu hợp, màu kỵ)
    private List<ProductDto> suggestedProducts; // Danh sách sản phẩm gợi ý
}