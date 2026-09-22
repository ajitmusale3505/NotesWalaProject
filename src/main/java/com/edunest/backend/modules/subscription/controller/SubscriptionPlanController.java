package com.edunest.backend.modules.subscription.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.subscription.dto.response.SubscriptionPlanResponse;
import com.edunest.backend.modules.subscription.service.SubscriptionPlanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> getAllPlans() {

        List<SubscriptionPlanResponse> plans =
                subscriptionPlanService.getAllPlans();

        ApiResponse<List<SubscriptionPlanResponse>> response =
                ApiResponse.<List<SubscriptionPlanResponse>>builder()
                        .success(true)
                        .message("Subscription plans fetched successfully")
                        .data(plans)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<SubscriptionPlanResponse>> getPlanBySlug(
            @PathVariable String slug) {

        SubscriptionPlanResponse plan =
                subscriptionPlanService.getPlanBySlug(slug);

        ApiResponse<SubscriptionPlanResponse> response =
                ApiResponse.<SubscriptionPlanResponse>builder()
                        .success(true)
                        .message("Subscription plan fetched successfully")
                        .data(plan)
                        .build();

        return ResponseEntity.ok(response);
    }
}