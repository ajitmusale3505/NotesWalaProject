package com.edunest.backend.modules.resourceentitlement.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resourceentitlement.dto.request.CreateResourceEntitlementRequest;
import com.edunest.backend.modules.resourceentitlement.dto.response.ResourceEntitlementResponse;
import com.edunest.backend.modules.resourceentitlement.entity.ResourceEntitlement;
import com.edunest.backend.modules.resourceentitlement.repository.ResourceEntitlementRepository;
import com.edunest.backend.modules.resourceentitlement.service.ResourceEntitlementService;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResourceEntitlementServiceImpl implements ResourceEntitlementService {

    private final ResourceEntitlementRepository entitlementRepository;
    private final ResourceRepository resourceRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public ResourceEntitlementResponse create(CreateResourceEntitlementRequest request) {

        Resource resource = resourceRepository.findById(request.getResourceId())
        		.orElseThrow(() ->
                new IllegalArgumentException(
                        "Resource not found with id: " + request.getResourceId()));

        SubscriptionPlan plan = subscriptionPlanRepository
                .findById(request.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));

        if (entitlementRepository.existsByResource_IdAndSubscriptionPlan_Id(
                resource.getId(),
                plan.getId())) {
            throw new BadRequestException("Entitlement already exists");
        }

        ResourceEntitlement entitlement = ResourceEntitlement.builder()
                .resource(resource)
                .subscriptionPlan(plan)
                .active(true)
                .build();

        entitlement = entitlementRepository.save(entitlement);

        return map(entitlement);
    }

    @Override
    public List<ResourceEntitlementResponse> getByResource(Long resourceId) {
        return entitlementRepository.findByResource_Id(resourceId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasEntitlement(Long resourceId, Long subscriptionPlanId) {
        return entitlementRepository.existsByResource_IdAndSubscriptionPlan_Id(
                resourceId,
                subscriptionPlanId
        );
    }

    private ResourceEntitlementResponse map(ResourceEntitlement entitlement) {
        return ResourceEntitlementResponse.builder()
                .id(entitlement.getId())
                .resourceId(entitlement.getResource().getId())
                .resourceTitle(entitlement.getResource().getTitle())
                .subscriptionPlanId(entitlement.getSubscriptionPlan().getId())
                .subscriptionPlanName(entitlement.getSubscriptionPlan().getName())
                .active(entitlement.isActive())
                .build();
    }
}