package com.edunest.backend.modules.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.dashboard.dto.DashboardResponse;
import com.edunest.backend.modules.dashboard.service.DashboardService;
import com.edunest.backend.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {

        Long userId = SecurityUtils.getCurrentUserId();

        DashboardResponse dashboard =
                dashboardService.getDashboard(userId);

        ApiResponse<DashboardResponse> response =
                ApiResponse.<DashboardResponse>builder()
                        .success(true)
                        .message("Dashboard fetched successfully")
                        .data(dashboard)
                        .build();

        return ResponseEntity.ok(response);
    }
}