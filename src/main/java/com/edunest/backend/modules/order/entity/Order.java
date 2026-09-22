package com.edunest.backend.modules.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.common.enums.OrderType;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_user_status", columnList = "user_id, status"),
        @Index(name = "idx_orders_order_number", columnList = "order_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business Order ID
    @Column(nullable = false, unique = true)
    private String orderNumber;

    // Buyer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Order Type
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    // Optional subscription purchase
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    // Pricing Breakdown
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Column(nullable = false, length = 10)
    private String currency;

    // Coupon
    private String couponCode;

    @Column(precision = 12, scale = 2)
    private BigDecimal couponDiscount;

    // Payment Metadata
    private String paymentProvider;

    private String paymentReference;

    private String paymentMethod;

    // Order Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // Invoice
    @Column(nullable = false)
    private boolean invoiceGenerated;

    private String invoiceUrl;

    // Business Timestamps
    private LocalDateTime paidAt;

    private LocalDateTime expiredAt;

    private LocalDateTime refundedAt;
}