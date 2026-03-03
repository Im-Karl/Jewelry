package com.example.jewelry.consultation.service;

import com.example.jewelry.consultation.dto.ConsultationResult;
import com.example.jewelry.product.domain.ProductRepository;
import com.example.jewelry.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final FengShuiCalculator calculator;
    private final ZodiacCalculator zodiacCalculator;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ConsultationResult getConsultation(int day, int month,int birthYear) {
        // 1. Tính mệnh
        String element = calculator.calculateElement(birthYear);

        String zodiac = zodiacCalculator.calculateZodiac(day, month);

        // 2. Lấy lời khuyên (Hardcode hoặc lấy từ DB bảng feng_shui_rules)
        String advice = getAdvice(element, zodiac);

        // 3. Tìm sản phẩm trong DB có fengShuiElement trùng khớp
        // Lưu ý: Trong ProductRepository bạn cần có method findByFengShuiElement
        List<ProductDto> products = productRepository.findByFengShuiElement(element)
                .stream()
                .map(p -> modelMapper.map(p, ProductDto.class))
                .collect(Collectors.toList());

        return ConsultationResult.builder()
                .element(element)
                .description(advice)
                .suggestedProducts(products)
                .build();
    }

    private String getAdvice(String element, String zodiac) {
        String elementAdvice = switch (element) {
            case "Kim" -> "Mệnh Kim hợp màu Trắng, Vàng, Xám. Nên đeo Bạc hoặc Vàng trắng.";
            case "Mộc" -> "Mệnh Mộc hợp màu Xanh lá, Đen, Xanh dương. Nên đeo đá Mắt Mèo hoặc Sapphire.";
            case "Thủy" -> "Mệnh Thủy hợp màu Đen, Trắng, Xanh dương. Nên đeo đá Aquamarine.";
            case "Hỏa" -> "Mệnh Hỏa hợp màu Đỏ, Hồng, Tím, Xanh lá. Nên đeo đá Ruby.";
            case "Thổ" -> "Mệnh Thổ hợp màu Vàng, Nâu, Đỏ. Nên đeo đá Thạch Anh Vàng.";
            default -> "Chúc bạn luôn may mắn.";
        };

        String zodiacAdvice = switch (zodiac) {
            case "Bạch Dương" -> "Cung Bạch Dương hợp thiết kế mạnh mẽ, cá tính.";
            case "Kim Ngưu" -> "Cung Kim Ngưu hợp trang sức sang trọng, tinh tế.";
            case "Song Tử" -> "Cung Song Tử hợp thiết kế trẻ trung, linh hoạt.";
            case "Cự Giải" -> "Cung Cự Giải hợp trang sức nhẹ nhàng, cảm xúc.";
            case "Sư Tử" -> "Cung Sư Tử hợp trang sức nổi bật, quyền lực.";
            case "Xử Nữ" -> "Cung Xử Nữ hợp thiết kế tối giản, thanh lịch.";
            case "Thiên Bình" -> "Cung Thiên Bình hợp trang sức cân đối, nghệ thuật.";
            case "Bọ Cạp" -> "Cung Bọ Cạp hợp trang sức bí ẩn, cá tính mạnh.";
            case "Nhân Mã" -> "Cung Nhân Mã hợp phong cách phóng khoáng.";
            case "Ma Kết" -> "Cung Ma Kết hợp trang sức cổ điển, bền vững.";
            case "Bảo Bình" -> "Cung Bảo Bình hợp thiết kế độc lạ.";
            case "Song Ngư" -> "Cung Song Ngư hợp trang sức mềm mại, lãng mạn.";
            default -> "";
        };

        return elementAdvice + " " + zodiacAdvice;
    }
}