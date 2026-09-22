package com.edunest.backend.modules.resourceanalytics.service;

import com.edunest.backend.modules.resourceanalytics.dto.request.TrackAnalyticsRequest;

public interface ResourceAnalyticsService {

    void trackEvent(TrackAnalyticsRequest request);
}