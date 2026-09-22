package com.edunest.backend.modules.resourceanalytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.resourceanalytics.dto.request.TrackAnalyticsRequest;
import com.edunest.backend.modules.resourceanalytics.service.ResourceAnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class ResourceAnalyticsController {

    private final ResourceAnalyticsService analyticsService;

    @PostMapping("/track")
    public ResponseEntity<ApiResponse<String>> track(
            @RequestBody TrackAnalyticsRequest request) {

        analyticsService.trackEvent(request);

        ApiResponse<String> response =
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Analytics tracked successfully")
                        .data("Tracked")
                        .build();

        return ResponseEntity.ok(response);
    }
}