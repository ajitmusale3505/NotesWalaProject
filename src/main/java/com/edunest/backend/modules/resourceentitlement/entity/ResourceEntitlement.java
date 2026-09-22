package com.edunest.backend.modules.resourceentitlement.entity;

import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_entitlements",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_resource_entitlement",
                columnNames = {"resource_id", "subscription_plan_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceEntitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Column(nullable = false)
    private boolean active;
}