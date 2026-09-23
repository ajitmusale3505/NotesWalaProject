package com.edunest.backend.modules.coupon.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.edunest.backend.common.enums.CouponScope;
import com.edunest.backend.common.enums.CouponType;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupons", indexes = {
        @Index(name = "idx_coupon_code", columnList = "code"),
        @Index(name = "idx_coupon_active_expiry", columnList = "active, expiry_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponType couponType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponScope scope;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal maximumDiscountAmount;

    @Column(nullable = false)
    private Integer maxUses;

    @Column(nullable = false)
    private Integer usedCount;

    @Column(nullable = false)
    private Integer maxUsesPerUser;

    private LocalDateTime startDate;
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "coupon_resources",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_coupon_resource",
                    columnNames = {"coupon_id", "resource_id"}))
    @Builder.Default
    private Set<Resource> resources = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "coupon_subscription_plans",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "subscription_plan_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_coupon_subscription_plan",
                    columnNames = {"coupon_id", "subscription_plan_id"}))
    @Builder.Default
    private Set<SubscriptionPlan> subscriptionPlans = new HashSet<>();
}
