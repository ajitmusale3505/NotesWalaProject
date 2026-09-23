package com.edunest.backend.modules.admin.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.admin.dto.*;
import com.edunest.backend.modules.admin.service.AdminCmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCmsController {

    private static final int MAX_PAGE_SIZE = 50;
    private final AdminCmsService adminCmsService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AdminCmsOverviewResponse>> overview() {
        return ResponseEntity.ok(ApiResponse.<AdminCmsOverviewResponse>builder()
                .success(true)
                .message("Admin CMS overview fetched successfully")
                .data(adminCmsService.getOverview())
                .build());
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        Page<AdminUserResponse> result = adminCmsService.getUsers(
                PageRequest.of(safePage, safeSize, Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id"))));

        return ResponseEntity.ok(ApiResponse.<Page<AdminUserResponse>>builder()
                .success(true)
                .message("Admin users fetched successfully")
                .data(result)
                .build());
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserUpdateRequest request) {

        return ResponseEntity.ok(ApiResponse.<AdminUserResponse>builder()
                .success(true)
                .message("User account updated successfully")
                .data(adminCmsService.updateUser(userId, request))
                .build());
    }
}