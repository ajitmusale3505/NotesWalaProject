package com.edunest.backend.modules.resourceanalytics.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.edunest.backend.common.enums.AnalyticsEventType;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resourceanalytics.dto.request.TrackAnalyticsRequest;
import com.edunest.backend.modules.resourceanalytics.entity.ResourceAnalytics;
import com.edunest.backend.modules.resourceanalytics.repository.ResourceAnalyticsRepository;
import com.edunest.backend.modules.resourceanalytics.service.ResourceAnalyticsService;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResourceAnalyticsServiceImpl
        implements ResourceAnalyticsService {

    private final ResourceAnalyticsRepository analyticsRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    @Override
    public void trackEvent(TrackAnalyticsRequest request) {

        Resource resource = resourceRepository
                .findById(request.getResourceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        User user = null;

        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));
        }

        ResourceAnalytics analytics = ResourceAnalytics.builder()
                .resource(resource)
                .user(user)
                .eventType(request.getEventType())
                .build();

        analyticsRepository.save(analytics);

        updateResourceCounters(resource, request.getEventType());
    }

    private void updateResourceCounters(
            Resource resource,
            AnalyticsEventType eventType) {

        if (resource.getDownloadsCount() == null) {
            resource.setDownloadsCount(0L);
        }

        if (resource.getPopularityScore() == null) {
            resource.setPopularityScore(0.0);
        }

        switch (eventType) {
            case PREVIEW -> resource.setPopularityScore(
                    resource.getPopularityScore() + 1
            );

            case VIEW -> resource.setPopularityScore(
                    resource.getPopularityScore() + 3
            );

            case DOWNLOAD -> {
                resource.setDownloadsCount(
                        resource.getDownloadsCount() + 1
                );

                resource.setPopularityScore(
                        resource.getPopularityScore() + 5
                );
            }

            case BOOKMARK -> resource.setPopularityScore(
                    resource.getPopularityScore() + 2
            );

            case SHARE -> resource.setPopularityScore(
                    resource.getPopularityScore() + 4
            );
        }

        resourceRepository.save(resource);
    }
}