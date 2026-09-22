package com.edunest.backend.modules.entitlement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.entitlement.service.EntitlementService;

import lombok.RequiredArgsConstructor;
import com.edunest.backend.security.util.SecurityUtils;

@RestController
@RequestMapping("/entitlements")
@RequiredArgsConstructor
public class EntitlementController {

    private final EntitlementService entitlementService;

    @GetMapping("/resource/{resourceId}/access")
    public ResponseEntity<ApiResponse<Boolean>> checkResourceAccess(
            @PathVariable Long resourceId) {

        Long userId = SecurityUtils.getCurrentUserId();

        boolean hasAccess =
                entitlementService.hasResourceAccess(userId, resourceId);

        ApiResponse<Boolean> response =
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("Resource access checked successfully")
                        .data(hasAccess)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/ai/access")
    public ResponseEntity<ApiResponse<Boolean>> checkAiAccess() {

        Long userId = SecurityUtils.getCurrentUserId();

        boolean canUseAi = entitlementService.canUseAi(userId);

        ApiResponse<Boolean> response =
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("AI access checked successfully")
                        .data(canUseAi)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/ai/consume")
    public ResponseEntity<ApiResponse<String>> consumeAiCredit() {

        Long userId = SecurityUtils.getCurrentUserId();

        entitlementService.consumeAiCredit(userId);

        ApiResponse<String> response =
                ApiResponse.<String>builder()
                        .success(true)
                        .message("AI credit consumed successfully")
                        .data("Remaining credits updated")
                        .build();

        return ResponseEntity.ok(response);
    }
    
    
}