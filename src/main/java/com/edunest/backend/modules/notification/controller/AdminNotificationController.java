package com.edunest.backend.modules.notification.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.notification.dto.request.AdminSystemNotificationRequest;
import com.edunest.backend.modules.notification.dto.request.AdminUniversityNotificationRequest;
import com.edunest.backend.modules.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final NotificationService notificationService;

    @PostMapping("/university")
    public ResponseEntity<ApiResponse<Void>> sendUniversityUpdate(
            @Valid @RequestBody AdminUniversityNotificationRequest request) {

        notificationService.createUniversityUpdateNotification(
                request.getUniversityId(),
                request.getTitle(),
                request.getMessage(),
                request.getReferenceId());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("University update notification sent")
                .build());
    }

    @PostMapping("/system")
    public ResponseEntity<ApiResponse<Void>> sendSystemNotification(
            @Valid @RequestBody AdminSystemNotificationRequest request) {

        notificationService.createSystemNotification(
                request.getTitle(),
                request.getMessage(),
                request.getReferenceId());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("System notification sent")
                .build());
    }
}
