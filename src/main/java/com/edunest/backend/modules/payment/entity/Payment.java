package com.edunest.backend.modules.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.common.enums.PaymentProvider;
import com.edunest.backend.common.enums.PaymentStatus;
import com.edunest.backend.modules.order.entity.Order;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Related Order
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Payment Gateway
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider provider;

    // Gateway References
    private String providerOrderId;

    private String providerPaymentId;

    private String providerSignature;

    // Payment Snapshot
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    // Payment Method
    private String paymentMethod;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // Failure Details
    private String failureCode;

    private String failureReason;

    // Refund Details
    @Column(precision = 12, scale = 2)
    private BigDecimal refundAmount;

    private String refundReference;

    private LocalDateTime refundedAt;

    // Webhook Payload
    @Column(columnDefinition = "TEXT")
    private String webhookPayload;

    // Business Timestamps
    private LocalDateTime paidAt;

    private LocalDateTime failedAt;
}