package com.edunest.backend.modules.userprofile.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.userprofile.dto.request.UserPersonalProfileRequest;
import com.edunest.backend.modules.userprofile.dto.request.UserPersonalProfilePatchRequest;
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

    /**
     * Dedicated About Me update endpoint. The frontend sends the About Me value
     * as a JSON string, keeping this small update independent from the larger
     * personal-profile PATCH payload.
     */
    @PatchMapping("/about-me")
    public ResponseEntity<ApiResponse<UserPersonalProfileResponse>> updateAboutMe(
            @RequestBody String aboutMe) {
        String value = aboutMe == null ? "" : aboutMe.trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        if (value.length() > 500) {
            throw new com.edunest.backend.common.exception.BadRequestException("About Me cannot exceed 500 characters");
        }
        return ResponseEntity.ok(ApiResponse.<UserPersonalProfileResponse>builder()
                .success(true)
                .message("About Me updated successfully")
                .data(personalProfileService.patchCurrent(
                        UserPersonalProfilePatchRequest.builder().aboutMe(value).build()))
                .build());
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserPersonalProfileResponse>> patchCurrent(
            @Valid @RequestBody UserPersonalProfilePatchRequest request) {
        return ResponseEntity.ok(ApiResponse.<UserPersonalProfileResponse>builder()
                .success(true)
                .message("Personal profile patched successfully")
                .data(personalProfileService.patchCurrent(request))
                .build());
    }
}
