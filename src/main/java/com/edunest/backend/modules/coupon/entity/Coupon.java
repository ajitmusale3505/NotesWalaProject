package com.edunest.backend.modules.coupon.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.edunest.backend.common.enums.CouponType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Coupon Code
    @Column(nullable = false, unique = true)
    private String code;

    // Discount
    @Column(nullable = false)
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType couponType;

    // Usage Limits
    private Integer maxUses;

    private Integer usedCount;

    // Expiry
    private LocalDateTime expiryDate;

    // Status
    @Column(nullable = false)
    private boolean active;
}