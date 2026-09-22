package com.edunest.backend.modules.resourceentitlement.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceEntitlementResponse {

    private Long id;

    private Long resourceId;
    private String resourceTitle;

    private Long subscriptionPlanId;
    private String subscriptionPlanName;

    private boolean active;
}