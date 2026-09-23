package com.edunest.backend.modules.resourceentitlement.service.impl;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resourceentitlement.entity.EntitlementSource;
import com.edunest.backend.modules.resourceentitlement.entity.UserResourceEntitlement;
import com.edunest.backend.modules.resourceentitlement.repository.ResourceEntitlementRepository;
import com.edunest.backend.modules.resourceentitlement.repository.UserResourceEntitlementRepository;
import com.edunest.backend.modules.resourceentitlement.service.UserResourceEntitlementService;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserResourceEntitlementServiceImpl implements UserResourceEntitlementService {

    private final UserResourceEntitlementRepository entitlementRepository;
    private final ResourceEntitlementRepository resourceEntitlementRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    @Transactional
    public UserResourceEntitlement grantPurchaseEntitlement(
            Long userId,
            Long resourceId,
            String purchaseReference) {

        User user = getUser(userId);
        Resource resource = getResource(resourceId);

        if (resource.getAccessType() != AccessType.DIRECT_PURCHASE) {
            throw new BadRequestException("Resource does not require a direct purchase");
        }

        return activate(
                user,
                resource,
                EntitlementSource.PURCHASE,
                null,
                LocalDateTime.now(),
                null,
                purchaseReference);
    }

    @Override
    @Transactional
    public UserResourceEntitlement grantSubscriptionEntitlement(
            Long userId,
            Long resourceId,
            Long subscriptionPlanId,
            LocalDateTime startsAt,
            LocalDateTime expiresAt,
            String subscriptionReference) {

        User user = getUser(userId);
        Resource resource = getResource(resourceId);

        if (resource.getAccessType() != AccessType.SUBSCRIPTION) {
            throw new BadRequestException("Resource does not require a subscription");
        }

        if (subscriptionPlanId == null) {
            throw new BadRequestException("Subscription plan is required");
        }

        if (startsAt == null || expiresAt == null || !expiresAt.isAfter(startsAt)) {
            throw new BadRequestException("Invalid entitlement validity period");
        }

        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscriptionPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));

        if (!plan.isActive()) {
            throw new BadRequestException("Subscription plan is inactive");
        }

        if (!resourceEntitlementRepository.existsByResource_IdAndSubscriptionPlan_IdAndActiveTrue(
                resource.getId(), plan.getId())) {
            throw new BadRequestException("Subscription plan does not include this resource");
        }

        return activate(
                user,
                resource,
                EntitlementSource.SUBSCRIPTION,
                plan,
                startsAt,
                expiresAt,
                subscriptionReference);
    }

    @Override
    public boolean hasActiveEntitlement(Long userId, Long resourceId) {
        return hasActiveEntitlement(userId, resourceId, EntitlementSource.PURCHASE.name())
                || hasActiveEntitlement(userId, resourceId, EntitlementSource.SUBSCRIPTION.name());
    }

    @Override
    public boolean hasActiveEntitlement(
            Long userId,
            Long resourceId,
            String source) {

        EntitlementSource entitlementSource = parseSource(source);
        return entitlementRepository.findActive(
                userId,
                resourceId,
                entitlementSource,
                LocalDateTime.now()).isPresent();
    }

    @Override
    @Transactional
    public void revokeEntitlement(
            Long userId,
            Long resourceId,
            String source) {

        EntitlementSource entitlementSource = parseSource(source);

        entitlementRepository.findByUser_IdAndResource_IdAndSource(
                        userId, resourceId, entitlementSource)
                .ifPresent(entitlement -> {
                    entitlement.setActive(false);
                    entitlementRepository.save(entitlement);
                });
    }

    @Override
    @Transactional
    public void expireEntitlement(Long entitlementId) {
        UserResourceEntitlement entitlement = entitlementRepository.findById(entitlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Entitlement not found"));

        entitlement.setActive(false);
        entitlementRepository.save(entitlement);
    }

    private UserResourceEntitlement activate(
            User user,
            Resource resource,
            EntitlementSource source,
            SubscriptionPlan plan,
            LocalDateTime startsAt,
            LocalDateTime expiresAt,
            String sourceReference) {

        UserResourceEntitlement entitlement =
                entitlementRepository.findByUser_IdAndResource_IdAndSource(
                        user.getId(), resource.getId(), source)
                .orElseGet(() -> UserResourceEntitlement.builder()
                        .user(user)
                        .resource(resource)
                        .source(source)
                        .build());

        entitlement.setSubscriptionPlan(plan);
        entitlement.setSourceReference(normalizeReference(sourceReference));
        entitlement.setStartsAt(startsAt);
        entitlement.setExpiresAt(expiresAt);
        entitlement.setActive(true);

        return entitlementRepository.save(entitlement);
    }

    private User getUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("User is required");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Resource getResource(Long resourceId) {
        if (resourceId == null) {
            throw new BadRequestException("Resource is required");
        }
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    private EntitlementSource parseSource(String source) {
        if (source == null || source.isBlank()) {
            throw new BadRequestException("Entitlement source is required");
        }
        try {
            return EntitlementSource.valueOf(source.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unsupported entitlement source");
        }
    }

    private String normalizeReference(String reference) {
        if (reference == null || reference.isBlank()) {
            return null;
        }
        String normalized = reference.trim();
        if (normalized.length() > 100) {
            throw new BadRequestException("Entitlement reference must be at most 100 characters");
        }
        return normalized;
    }
}
