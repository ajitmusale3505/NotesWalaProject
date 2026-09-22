package com.edunest.backend.modules.subscription.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {
    private Long userId;
    private Long planId;
}