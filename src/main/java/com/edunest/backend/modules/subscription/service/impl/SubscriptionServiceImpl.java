package com.edunest.backend.modules.subscription.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.edunest.backend.common.enums.SubscriptionStatus;
import com.edunest.backend.modules.subscription.dto.request.CreateSubscriptionRequest;
import com.edunest.backend.modules.subscription.dto.response.SubscriptionResponse;
import com.edunest.backend.modules.subscription.entity.Subscription;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;
import com.edunest.backend.modules.subscription.repository.SubscriptionRepository;
import com.edunest.backend.modules.subscription.service.SubscriptionService;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    @Value("${payment.subscription.allow-direct-activation:false}")
    private boolean allowDirectActivation;

    @Override
    @Transactional
    public SubscriptionResponse purchase(CreateSubscriptionRequest request) {

        if (!allowDirectActivation) {
            throw new BadRequestException(
                    "Direct subscription activation is disabled until the payment flow is configured");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        boolean alreadyActive = subscriptionRepository
                .findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .filter(sub -> sub.getEndDate() != null
                        && sub.getEndDate().isAfter(LocalDateTime.now()))
                .isPresent();

        if (alreadyActive) {
            throw new BadRequestException("User already has an active subscription");
        }

        LocalDateTime now = LocalDateTime.now();

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(now)
                .endDate(now.plusDays(plan.getValidityDays()))
                .autoRenew(false)
                .build();

        subscription = subscriptionRepository.save(subscription);

        return map(subscription);
    }

    @Override
    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return subscriptionRepository.findByUser(user)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasActiveSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return subscriptionRepository
                .findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .filter(sub -> sub.getEndDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Override
    @Transactional
    public SubscriptionResponse cancel(Long subscriptionId) {

        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        Long currentUserId = com.edunest.backend.security.util.SecurityUtils.getCurrentUserId();
        boolean admin = com.edunest.backend.security.util.SecurityUtils.isAdmin();
        if (!admin && !currentUserId.equals(sub.getUser().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not allowed to cancel this subscription");
        }

        sub.setStatus(SubscriptionStatus.CANCELLED);
        sub.setCancelledAt(LocalDateTime.now());

        subscriptionRepository.save(sub);

        return map(sub);
    }

    private SubscriptionResponse map(Subscription sub) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .userId(sub.getUser().getId())
                .planId(sub.getPlan().getId())
                .planName(sub.getPlan().getName())
                .status(sub.getStatus())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .build();
    }
}