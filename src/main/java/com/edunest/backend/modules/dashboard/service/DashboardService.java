package com.edunest.backend.modules.dashboard.service;

import com.edunest.backend.modules.dashboard.dto.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboard(Long userId);
}