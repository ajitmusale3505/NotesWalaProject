package com.edunest.backend.modules.resourceentitlement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.resourceentitlement.dto.request.CreateResourceEntitlementRequest;
import com.edunest.backend.modules.resourceentitlement.dto.response.ResourceEntitlementResponse;
import com.edunest.backend.modules.resourceentitlement.service.ResourceEntitlementService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/resource-entitlements")
@RequiredArgsConstructor
public class ResourceEntitlementController {

    private final ResourceEntitlementService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceEntitlementResponse create(
            @RequestBody CreateResourceEntitlementRequest request) {
        return service.create(request);
    }

    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ResourceEntitlementResponse> getByResource(
            @PathVariable Long resourceId) {
        return service.getByResource(resourceId);
    }

    @GetMapping("/check")
    public boolean checkAccess(
            @RequestParam Long resourceId,
            @RequestParam Long subscriptionPlanId) {

        return service.hasEntitlement(resourceId, subscriptionPlanId);
    }
}