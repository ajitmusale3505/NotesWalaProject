package com.edunest.backend.modules.userprofile.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.userprofile.dto.request.UserSocialLinksPatchRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserSocialLinksResponse;
import com.edunest.backend.modules.userprofile.service.UserSocialLinksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-profile/social-links")
@RequiredArgsConstructor
public class UserSocialLinksController {

    private final UserSocialLinksService socialLinksService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserSocialLinksResponse>> getCurrent() {
        return ResponseEntity.ok(ApiResponse.<UserSocialLinksResponse>builder()
                .success(true)
                .message("Social links fetched successfully")
                .data(socialLinksService.getCurrent())
                .build());
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserSocialLinksResponse>> patchCurrent(
            @Valid @RequestBody UserSocialLinksPatchRequest request) {
        return ResponseEntity.ok(ApiResponse.<UserSocialLinksResponse>builder()
                .success(true)
                .message("Social links updated successfully")
                .data(socialLinksService.patchCurrent(request))
                .build());
    }
}
