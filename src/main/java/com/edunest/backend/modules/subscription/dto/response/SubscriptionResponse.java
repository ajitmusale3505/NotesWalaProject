package com.edunest.backend.modules.subscription.dto.response;

import java.time.LocalDateTime;

import com.edunest.backend.common.enums.SubscriptionStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Long id;
    private Long userId;
    private Long planId;
    private String planName;
    private SubscriptionStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}