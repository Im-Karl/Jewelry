package com.example.jewelry.product.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String slug;

    private String iconUrl; // Dùng cho Visual Filter (Vòng, Nhẫn...)

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}