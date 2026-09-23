package com.edunest.backend.modules.resourceentitlement.service;

import com.edunest.backend.modules.resourceentitlement.entity.UserResourceEntitlement;

import java.time.LocalDateTime;

public interface UserResourceEntitlementService {

    UserResourceEntitlement grantPurchaseEntitlement(
            Long userId,
            Long resourceId,
            String purchaseReference);

    UserResourceEntitlement grantSubscriptionEntitlement(
            Long userId,
            Long resourceId,
            Long subscriptionPlanId,
            LocalDateTime startsAt,
            LocalDateTime expiresAt,
            String subscriptionReference);

    boolean hasActiveEntitlement(Long userId, Long resourceId);

    boolean hasActiveEntitlement(
            Long userId,
            Long resourceId,
            String source);

    void revokeEntitlement(
            Long userId,
            Long resourceId,
            String source);

    void expireEntitlement(Long entitlementId);
}
