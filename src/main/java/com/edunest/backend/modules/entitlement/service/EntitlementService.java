package com.edunest.backend.modules.entitlement.service;

public interface EntitlementService {

    void grantResourceEntitlement(
            Long userId,
            Long resourceId);

    void grantSubscriptionEntitlement(
            Long userId,
            Long subscriptionPlanId);

    boolean hasResourceAccess(
            Long userId,
            Long resourceId);

    boolean canUseAi(Long userId);

    void consumeAiCredit(Long userId);

    void consumeDownload(Long userId);
}