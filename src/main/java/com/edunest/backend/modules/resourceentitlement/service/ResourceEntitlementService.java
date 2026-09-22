package com.edunest.backend.modules.resourceentitlement.service;

import java.util.List;

import com.edunest.backend.modules.resourceentitlement.dto.request.CreateResourceEntitlementRequest;
import com.edunest.backend.modules.resourceentitlement.dto.response.ResourceEntitlementResponse;

public interface ResourceEntitlementService {

    ResourceEntitlementResponse create(CreateResourceEntitlementRequest request);

    List<ResourceEntitlementResponse> getByResource(Long resourceId);

    boolean hasEntitlement(Long resourceId, Long subscriptionPlanId);
}