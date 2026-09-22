package com.edunest.backend.modules.subscription.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.subscription.dto.request.CreateSubscriptionRequest;
import com.edunest.backend.modules.subscription.dto.response.SubscriptionResponse;
import com.edunest.backend.modules.subscription.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import com.edunest.backend.security.util.SecurityUtils;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/purchase")
    public SubscriptionResponse purchase(
            @RequestBody CreateSubscriptionRequest request) {
        request.setUserId(SecurityUtils.getCurrentUserId());
        return subscriptionService.purchase(request);
    }

    @GetMapping("/user/{userId}")
    public List<SubscriptionResponse> getUserSubscriptions(
            @PathVariable Long userId) {
        SecurityUtils.requireSameUserOrAdmin(userId);
        return subscriptionService.getUserSubscriptions(userId);
    }

    @GetMapping("/check-active/{userId}")
    public boolean hasActiveSubscription(
            @PathVariable Long userId) {
        SecurityUtils.requireSameUserOrAdmin(userId);
        return subscriptionService.hasActiveSubscription(userId);
    }

    @PostMapping("/cancel/{subscriptionId}")
    public SubscriptionResponse cancel(
            @PathVariable Long subscriptionId) {
        return subscriptionService.cancel(subscriptionId);
    }
}