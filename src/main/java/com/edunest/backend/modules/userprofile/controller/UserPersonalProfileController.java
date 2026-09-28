package com.edunest.backend.modules.userprofile.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.userprofile.dto.request.UserPersonalProfileRequest;
import com.edunest.backend.modules.userprofile.dto.response.UserPersonalProfileResponse;
import com.edunest.backend.modules.userprofile.service.UserPersonalProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-profile/personal")
@RequiredArgsConstructor
public class UserPersonalProfileController {

    private final UserPersonalProfileService personalProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserPersonalProfileResponse>> getCurrent() {
        return ResponseEntity.ok(ApiResponse.<UserPersonalProfileResponse>builder()
                .success(true)
                .message("Personal profile fetched successfully")
                .data(personalProfileService.getCurrent())
                .build());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserPersonalProfileResponse>> updateCurrent(
            @Valid @RequestBody UserPersonalProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.<UserPersonalProfileResponse>builder()
                .success(true)
                .message("Personal profile updated successfully")
                .data(personalProfileService.updateCurrent(request))
                .build());
    }
}
