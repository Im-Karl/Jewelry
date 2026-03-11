package com.example.jewelry.order.domain;

import com.example.jewelry.product.domain.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    // Lưu giá tại thời điểm mua (đề phòng sau này giá sản phẩm thay đổi)
    private BigDecimal priceAtPurchase;

    private UUID variantId;

    @Column(name = "variant_size")
    private String size;

    @Column(name = "variant_color")
    private String color;
}