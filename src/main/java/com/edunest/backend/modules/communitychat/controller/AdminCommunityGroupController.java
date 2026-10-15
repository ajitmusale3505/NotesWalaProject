package com.edunest.backend.modules.communitychat.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.communitychat.dto.request.AdminCommunityGroupRequest;
import com.edunest.backend.modules.communitychat.dto.response.AdminCommunityGroupResponse;
import com.edunest.backend.modules.communitychat.dto.response.AdminCommunityGroupMemberResponse;
import com.edunest.backend.modules.communitychat.dto.response.AdminCommunityGroupStatisticsResponse;
import com.edunest.backend.modules.communitychat.dto.response.AdminCommunityGroupSummaryResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.service.AdminCommunityGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/community-groups")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommunityGroupController {

    private final AdminCommunityGroupService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminCommunityGroupResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String universityId,
            @RequestParam(defaultValue = "all") String status) {
        return ResponseEntity.ok(ApiResponse.<Page<AdminCommunityGroupResponse>>builder()
                .success(true)
                .message("Community groups fetched successfully.")
                .data(service.list(page, size, query, universityId, status))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCommunityGroupResponse>> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<AdminCommunityGroupResponse>builder()
                .success(true)
                .message("Community group fetched successfully.")
                .data(service.getGroup(id))
                .build());
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<ApiResponse<AdminCommunityGroupStatisticsResponse>> statistics(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<AdminCommunityGroupStatisticsResponse>builder()
                .success(true)
                .message("Community group statistics fetched successfully.")
                .data(service.statistics(id))
                .build());
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<Page<AdminCommunityGroupMemberResponse>>> members(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "all") String activityLevel) {
        return ResponseEntity.ok(ApiResponse.<Page<AdminCommunityGroupMemberResponse>>builder()
                .success(true)
                .message("Community group members fetched successfully.")
                .data(service.members(id, page, size, query, status, activityLevel))
                .build());
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<Page<CommunityMessageResponse>>> messages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.<Page<CommunityMessageResponse>>builder()
                .success(true)
                .message("Community group messages fetched successfully.")
                .data(service.messages(id, page, size))
                .build());
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AdminCommunityGroupSummaryResponse>> summary() {
        return ResponseEntity.ok(ApiResponse.<AdminCommunityGroupSummaryResponse>builder()
                .success(true)
                .message("Community group summary fetched successfully.")
                .data(service.summary())
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminCommunityGroupResponse>> create(
            @Valid @RequestBody AdminCommunityGroupRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.<AdminCommunityGroupResponse>builder()
                .success(true)
                .message("Community group created successfully.")
                .data(service.create(request))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCommunityGroupResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminCommunityGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.<AdminCommunityGroupResponse>builder()
                .success(true)
                .message("Community group updated successfully.")
                .data(service.update(id, request))
                .build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminCommunityGroupResponse>> updateStatus(
            @PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(ApiResponse.<AdminCommunityGroupResponse>builder()
                .success(true)
                .message(active ? "Community group activated." : "Community group deactivated.")
                .data(service.changeStatus(id, active))
                .build());
    }

    @PostMapping(value = "/{id}/icon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AdminCommunityGroupResponse>> uploadIcon(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.<AdminCommunityGroupResponse>builder()
                .success(true)
                .message("Community group icon uploaded successfully.")
                .data(service.uploadIcon(id, file))
                .build());
    }
}
