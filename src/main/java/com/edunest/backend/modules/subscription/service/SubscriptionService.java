package com.edunest.backend.modules.subscription.service;

import java.util.List;

import com.edunest.backend.modules.subscription.dto.request.CreateSubscriptionRequest;
import com.edunest.backend.modules.subscription.dto.response.SubscriptionResponse;

public interface SubscriptionService {

    SubscriptionResponse purchase(CreateSubscriptionRequest request);

    List<SubscriptionResponse> getUserSubscriptions(Long userId);

    boolean hasActiveSubscription(Long userId);

    SubscriptionResponse cancel(Long subscriptionId);
}