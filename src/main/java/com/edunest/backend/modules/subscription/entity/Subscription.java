package com.edunest.backend.modules.subscription.entity;

import java.time.LocalDateTime;

import com.edunest.backend.common.enums.SubscriptionStatus;
import com.edunest.backend.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscription_user_status", columnList = "user_id, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Selected Plan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    // Lifecycle
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime cancelledAt;

    // Renewal
    @Column(nullable = false)
    private boolean autoRenew;
}