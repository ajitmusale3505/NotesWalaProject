package com.edunest.backend.modules.entitlement.entity;

import java.time.LocalDateTime;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.common.enums.EntitlementSource;
import com.edunest.backend.common.enums.EntitlementStatus;
import com.edunest.backend.common.enums.EntitlementType;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.subscription.entity.Subscription;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "entitlements", indexes = {
        @Index(name = "idx_entitlement_user_type_status", columnList = "user_id, entitlementType, status"),
        @Index(name = "idx_entitlement_resource_status", columnList = "resource_id, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entitlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Resource purchase or subscription entitlement
    @Enumerated(EnumType.STRING)
    @Column(name = "entitlement_type", nullable = false)
    private EntitlementType entitlementType;

    // Optional for individual purchased resources
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    // Optional for subscription access
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntitlementStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntitlementSource source;

    // Access validity
    @Column(nullable = false)
    private LocalDateTime grantedAt;

    private LocalDateTime expiresAt;

    // Runtime usage state
    private Integer remainingAiCredits;

    // CSV subject IDs for Turbo/Titan
    @Column(columnDefinition = "TEXT")
    private String selectedSubjectIds;

    @Column(columnDefinition = "TEXT")
    private String notes;
}