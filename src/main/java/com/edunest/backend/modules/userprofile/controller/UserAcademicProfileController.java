package com.edunest.backend.modules.userprofile.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfilePatchRequest;
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfileRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserAcademicProfileResponse;
import com.edunest.backend.modules.userprofile.service.UserAcademicProfileService;
import com.edunest.backend.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-profile")
@RequiredArgsConstructor
public class UserAcademicProfileController {

    private final UserAcademicProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserAcademicProfileResponse>> getProfile(@PathVariable Long userId) {
        SecurityUtils.requireSameUserOrAdmin(userId);
        return ResponseEntity.ok(ApiResponse.<UserAcademicProfileResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(profileService.getByUserId(userId))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAcademicProfileResponse>> saveProfile(
            @Valid @RequestBody UserAcademicProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.<UserAcademicProfileResponse>builder()
                .success(true)
                .message("Profile saved successfully")
                .data(profileService.saveProfile(userId, request))
                .build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserAcademicProfileResponse>> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserAcademicProfileRequest request) {
        SecurityUtils.requireSameUserOrAdmin(userId);
        return ResponseEntity.ok(ApiResponse.<UserAcademicProfileResponse>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(profileService.updateProfile(userId, request))
                .build());
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserAcademicProfileResponse>> patchProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserAcademicProfilePatchRequest request) {
        SecurityUtils.requireSameUserOrAdmin(userId);
        return ResponseEntity.ok(ApiResponse.<UserAcademicProfileResponse>builder()
                .success(true)
                .message("Profile patched successfully")
                .data(profileService.patchProfile(userId, request))
                .build());
    }
}