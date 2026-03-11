package com.example.jewelry.product.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data // Tạo Getter, Setter, toString...
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal basePrice;

    // --- Tech Specs (Thông số kỹ thuật) ---
    private String materialType; // Bạc 925, Bạc Thái
    private String stoneType;    // Đá CZ, Đá Mắt Mèo
    private String platingColor; // Xi vàng trắng, Xi hồng

    // --- Killer Features ---
    private String fengShuiElement; // Kim, Mộc, Thủy... (Để Chatbot query)

    private boolean isArEnabled;
    private String arModelUrl; // Link file .glb cho AR Try-on

    // --- Media ---
    private String mainImageUrl;
    // (Thực tế nên có bảng ProductImage riêng, nhưng để đơn giản ta dùng chuỗi JSON hoặc list ảnh ở đây nếu cần)

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants;

    private boolean isDeleted = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}