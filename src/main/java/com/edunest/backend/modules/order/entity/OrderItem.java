package com.edunest.backend.modules.order.entity;

import java.math.BigDecimal;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.modules.resource.entity.Resource;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "idx_order_items_resource", columnList = "resource_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Parent Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Original Resource
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    // Snapshot Product Info
    @Column(nullable = false)
    private String resourceTitleSnapshot;

    @Enumerated(EnumType.STRING)
    private MaterialType materialTypeSnapshot;

    // Snapshot Pricing
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal finalUnitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    // Snapshot Metadata
    private Integer pageCountSnapshot;

    private Long fileSizeBytesSnapshot;
}