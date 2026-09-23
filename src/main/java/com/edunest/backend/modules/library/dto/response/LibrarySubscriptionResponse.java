package com.edunest.backend.modules.library.dto.response;

import com.edunest.backend.common.enums.SubscriptionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibrarySubscriptionResponse {

    private Long subscriptionId;
    private Long planId;
    private String planName;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
