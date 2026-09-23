package com.edunest.backend.modules.resourceentitlement.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_resource_entitlements",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_resource_entitlement_source",
                columnNames = {"user_id", "resource_id", "source"}),
        indexes = {
                @Index(name = "idx_user_resource_entitlement_user_resource",
                        columnList = "user_id, resource_id"),
                @Index(name = "idx_user_resource_entitlement_active_expiry",
                        columnList = "active, expires_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResourceEntitlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntitlementSource source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "source_reference", length = 100)
    private String sourceReference;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean active;

    @Version
    @Column(nullable = false)
    private Long version;
}
