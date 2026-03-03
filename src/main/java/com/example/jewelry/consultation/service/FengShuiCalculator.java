package com.example.jewelry.consultation.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class FengShuiCalculator {

    private static final String[] CAN = {
            "Canh", "Tân", "Nhâm", "Quý",
            "Giáp", "Ất", "Bính", "Đinh",
            "Mậu", "Kỷ"
    };

    private static final String[] CHI = {
            "Thân", "Dậu", "Tuất", "Hợi",
            "Tý", "Sửu", "Dần", "Mão",
            "Thìn", "Tỵ", "Ngọ", "Mùi"
    };

    // 60 Hoa Giáp → Ngũ hành (Nạp Âm)
    private static final Map<String, String> NAP_AM_MAP;

    static {
        Map<String, String> map = new HashMap<>();

        // Hải Trung Kim
        map.put("GiápTý", "Kim");
        map.put("ẤtSửu", "Kim");

        // Lư Trung Hỏa
        map.put("BínhDần", "Hỏa");
        map.put("ĐinhMão", "Hỏa");

        // Đại Lâm Mộc
        map.put("MậuThìn", "Mộc");
        map.put("KỷTỵ", "Mộc");

        // Lộ Bàng Thổ
        map.put("CanhNgọ", "Thổ");
        map.put("TânMùi", "Thổ");

        // Kiếm Phong Kim
        map.put("NhâmThân", "Kim");
        map.put("QuýDậu", "Kim");

        // Sơn Đầu Hỏa
        map.put("GiápTuất", "Hỏa");
        map.put("ẤtHợi", "Hỏa");

        // Giản Hạ Thủy
        map.put("BínhTý", "Thủy");
        map.put("ĐinhSửu", "Thủy");

        // Thành Đầu Thổ
        map.put("MậuDần", "Thổ");
        map.put("KỷMão", "Thổ");

        // Bạch Lạp Kim
        map.put("CanhThìn", "Kim");
        map.put("TânTỵ", "Kim");

        // Dương Liễu Mộc
        map.put("NhâmNgọ", "Mộc");
        map.put("QuýMùi", "Mộc");

        // Tuyền Trung Thủy
        map.put("GiápThân", "Thủy");
        map.put("ẤtDậu", "Thủy");

        // Ốc Thượng Thổ
        map.put("BínhTuất", "Thổ");
        map.put("ĐinhHợi", "Thổ");

        // Tích Lịch Hỏa
        map.put("MậuTý", "Hỏa");
        map.put("KỷSửu", "Hỏa");

        // Tùng Bách Mộc
        map.put("CanhDần", "Mộc");
        map.put("TânMão", "Mộc");

        // Trường Lưu Thủy
        map.put("NhâmThìn", "Thủy");
        map.put("QuýTỵ", "Thủy");

        // Sa Trung Kim
        map.put("GiápNgọ", "Kim");
        map.put("ẤtMùi", "Kim");

        // Sơn Hạ Hỏa
        map.put("BínhThân", "Hỏa");
        map.put("ĐinhDậu", "Hỏa");

        // Bình Địa Mộc
        map.put("MậuTuất", "Mộc");
        map.put("KỷHợi", "Mộc");

        // Bích Thượng Thổ
        map.put("CanhTý", "Thổ");
        map.put("TânSửu", "Thổ");

        // Kim Bạch Kim
        map.put("NhâmDần", "Kim");
        map.put("QuýMão", "Kim");

        // Phú Đăng Hỏa
        map.put("GiápThìn", "Hỏa");
        map.put("ẤtTỵ", "Hỏa");

        // Thiên Hà Thủy
        map.put("BínhNgọ", "Thủy");
        map.put("ĐinhMùi", "Thủy");

        // Đại Trạch Thổ
        map.put("MậuThân", "Thổ");
        map.put("KỷDậu", "Thổ");

        // Thoa Xuyến Kim
        map.put("CanhTuất", "Kim");
        map.put("TânHợi", "Kim");

        // Tang Đố Mộc
        map.put("NhâmTý", "Mộc");
        map.put("QuýSửu", "Mộc");

        // Đại Khê Thủy
        map.put("GiápDần", "Thủy");
        map.put("ẤtMão", "Thủy");

        // Sa Trung Thổ
        map.put("BínhThìn", "Thổ");
        map.put("ĐinhTỵ", "Thổ");

        // Thiên Thượng Hỏa
        map.put("MậuNgọ", "Hỏa");
        map.put("KỷMùi", "Hỏa");

        // Thạch Lựu Mộc
        map.put("CanhThân", "Mộc");
        map.put("TânDậu", "Mộc");

        // Đại Hải Thủy
        map.put("NhâmTuất", "Thủy");
        map.put("QuýHợi", "Thủy");

        // khóa map lại, không cho sửa
        NAP_AM_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * Tính mệnh ngũ hành chuẩn theo Nạp Âm
     */
    public String calculateElement(int birthYear) {
        String can = CAN[birthYear % 10];
        String chi = CHI[birthYear % 12];
        String key = can + chi;

        return NAP_AM_MAP.getOrDefault(key, "Unknown");
    }

    /**
     * Lấy đầy đủ Can Chi (để hiển thị cho frontend)
     */
    public String getCanChi(int birthYear) {
        return CAN[birthYear % 10] + " " + CHI[birthYear % 12];
    }
}
