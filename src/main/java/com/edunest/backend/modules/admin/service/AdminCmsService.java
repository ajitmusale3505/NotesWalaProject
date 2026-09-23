package com.edunest.backend.modules.admin.service;

import com.edunest.backend.modules.admin.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminCmsService {
    AdminCmsOverviewResponse getOverview();
    Page<AdminUserResponse> getUsers(Pageable pageable);
    AdminUserResponse updateUser(Long userId, AdminUserUpdateRequest request);
}