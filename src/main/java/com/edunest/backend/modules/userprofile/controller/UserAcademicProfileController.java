package com.edunest.backend.modules.userprofile.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfileRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserAcademicProfileResponse;
import com.edunest.backend.modules.userprofile.service.UserAcademicProfileService;

import com.edunest.backend.modules.userprofile.dto.request.UserAcademicProfilePatchRequest;

import jakarta.validation.Valid;
import com.edunest.backend.security.util.SecurityUtils;

@RestController
@RequestMapping("/user-profile")
public class UserAcademicProfileController {

    private final UserAcademicProfileService profileService;

    public UserAcademicProfileController(
            UserAcademicProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserAcademicProfileResponse>> getProfile(
            @PathVariable Long userId) {

        SecurityUtils.requireSameUserOrAdmin(userId);
        UserAcademicProfileResponse profile =
                profileService.getByUserId(userId);

        ApiResponse<UserAcademicProfileResponse> response =
                ApiResponse.<UserAcademicProfileResponse>builder()
                        .success(true)
                        .message("Profile fetched successfully")
                        .data(profile)
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAcademicProfileResponse>> saveProfile(
            @Valid @RequestBody UserAcademicProfileRequest request) {

        request.setUserId(SecurityUtils.getCurrentUserId());
        UserAcademicProfileResponse profile =
                profileService.saveProfile(request);

        ApiResponse<UserAcademicProfileResponse> response =
                ApiResponse.<UserAcademicProfileResponse>builder()
                        .success(true)
                        .message("Profile saved successfully")
                        .data(profile)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserAcademicProfileResponse>> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserAcademicProfileRequest request) {

        SecurityUtils.requireSameUserOrAdmin(userId);
        UserAcademicProfileResponse profile =
                profileService.updateProfile(userId, request);

        ApiResponse<UserAcademicProfileResponse> response =
                ApiResponse.<UserAcademicProfileResponse>builder()
                        .success(true)
                        .message("Profile updated successfully")
                        .data(profile)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserAcademicProfileResponse>> patchProfile(
            @PathVariable Long userId,
            @RequestBody UserAcademicProfilePatchRequest request) {

        SecurityUtils.requireSameUserOrAdmin(userId);
        UserAcademicProfileResponse profile =
                profileService.patchProfile(userId, request);

        ApiResponse<UserAcademicProfileResponse> response =
                ApiResponse.<UserAcademicProfileResponse>builder()
                        .success(true)
                        .message("Profile patched successfully")
                        .data(profile)
                        .build();

        return ResponseEntity.ok(response);
    }
}