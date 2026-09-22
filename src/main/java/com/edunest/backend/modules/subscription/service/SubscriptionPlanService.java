package com.edunest.backend.modules.subscription.service;

import java.util.List;

import com.edunest.backend.modules.subscription.dto.response.SubscriptionPlanResponse;

public interface SubscriptionPlanService {

    List<SubscriptionPlanResponse> getAllPlans();

    SubscriptionPlanResponse getPlanBySlug(String slug);
}