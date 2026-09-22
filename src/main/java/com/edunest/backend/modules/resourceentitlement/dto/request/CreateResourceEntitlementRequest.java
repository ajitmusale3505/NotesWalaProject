package com.edunest.backend.modules.resourceentitlement.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateResourceEntitlementRequest {

    private Long resourceId;
    private Long subscriptionPlanId;
}