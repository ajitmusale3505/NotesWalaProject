package com.edunest.backend.modules.resourceanalytics.dto.request;

import com.edunest.backend.common.enums.AnalyticsEventType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackAnalyticsRequest {

    private Long resourceId;
    private Long userId;
    private AnalyticsEventType eventType;
}