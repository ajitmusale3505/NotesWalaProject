package com.edunest.backend.modules.entitlement.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edunest.backend.common.enums.EntitlementSource;
import com.edunest.backend.common.enums.EntitlementStatus;
import com.edunest.backend.common.enums.EntitlementType;
import com.edunest.backend.common.enums.SubscriptionStatus;
import com.edunest.backend.modules.entitlement.entity.Entitlement;
import com.edunest.backend.modules.entitlement.repository.EntitlementRepository;
import com.edunest.backend.modules.entitlement.service.EntitlementService;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.subscription.entity.Subscription;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;
import com.edunest.backend.modules.subscription.repository.SubscriptionRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EntitlementServiceImpl implements EntitlementService {

    private final EntitlementRepository entitlementRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public void grantResourceEntitlement(Long userId, Long resourceId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        boolean exists =
                entitlementRepository.existsByUserIdAndResourceIdAndStatus(
                        userId,
                        resourceId,
                        EntitlementStatus.ACTIVE);

        if (exists) {
            return;
        }

        Entitlement entitlement = Entitlement.builder()
                .user(user)
                .entitlementType(EntitlementType.RESOURCE)
                .resource(resource)
                .status(EntitlementStatus.ACTIVE)
                .source(EntitlementSource.ORDER)
                .grantedAt(LocalDateTime.now())
                .build();

        entitlementRepository.save(entitlement);
    }

    @Override
    public void grantSubscriptionEntitlement(
            Long userId,
            Long subscriptionPlanId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SubscriptionPlan plan =
                subscriptionPlanRepository.findById(subscriptionPlanId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Plan not found"));

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = plan.getValidityDays() != null
                ? start.plusDays(plan.getValidityDays())
                : null;

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(start)
                .endDate(end)
                .autoRenew(false)
                .build();

        Subscription savedSubscription =
                subscriptionRepository.save(subscription);

        Integer aiCredits = plan.isAiAccess()
                ? java.util.Optional.ofNullable(plan.getAiCreditsPerPeriod()).orElse(0)
                : 0;

        Entitlement entitlement = Entitlement.builder()
                .user(user)
                .entitlementType(EntitlementType.SUBSCRIPTION)
                .subscription(savedSubscription)
                .subscriptionPlan(plan)
                .status(EntitlementStatus.ACTIVE)
                .source(EntitlementSource.ORDER)
                .grantedAt(start)
                .expiresAt(end)
                .remainingAiCredits(aiCredits)
                .selectedSubjectIds(null)
                .build();

        entitlementRepository.save(entitlement);
    }

    @Override
    public boolean hasResourceAccess(Long userId, Long resourceId) {
        return entitlementRepository
                .existsByUserIdAndResourceIdAndStatus(
                        userId,
                        resourceId,
                        EntitlementStatus.ACTIVE);
    }

    @Override
    public boolean canUseAi(Long userId) {

        Entitlement entitlement =
                entitlementRepository
                        .findByUserIdAndEntitlementTypeAndStatus(
                                userId,
                                EntitlementType.SUBSCRIPTION,
                                EntitlementStatus.ACTIVE)
                        .orElse(null);

        if (entitlement == null) {
            return false;
        }

        SubscriptionPlan plan = entitlement.getSubscriptionPlan();

        if (!plan.isAiAccess()) {
            return false;
        }

        Integer credits = entitlement.getRemainingAiCredits();

        return credits != null && credits > 0;
    }

    @Override
    public void consumeAiCredit(Long userId) {

        Entitlement entitlement =
                entitlementRepository
                        .findByUserIdAndEntitlementTypeAndStatus(
                                userId,
                                EntitlementType.SUBSCRIPTION,
                                EntitlementStatus.ACTIVE)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "No active entitlement"));

        Integer credits = entitlement.getRemainingAiCredits();
        if (credits == null || credits <= 0) {
            throw new BadRequestException("No AI credits left");
        }

        int updated = entitlementRepository.decrementAiCredit(
                userId,
                EntitlementType.SUBSCRIPTION,
                EntitlementStatus.ACTIVE);

        if (updated != 1) {
            throw new BadRequestException("No AI credits left");
        }
    }

    @Override
    public void consumeDownload(Long userId) {
        throw new UnsupportedOperationException(
                "Download limit logic removed in new architecture");
    }
}