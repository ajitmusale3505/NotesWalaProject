package com.edunest.backend.modules.notification.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.notification.dto.response.NotificationResponse;
import com.edunest.backend.modules.notification.dto.response.UnreadCountResponse;
import com.edunest.backend.modules.notification.service.NotificationService;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.<Page<NotificationResponse>>builder()
                .success(true)
                .message("Notifications fetched successfully")
                .data(notificationService.getMyNotifications(
                        SecurityUtils.getCurrentUserId(), page, size))
                .build());
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount() {
        return ResponseEntity.ok(ApiResponse.<UnreadCountResponse>builder()
                .success(true)
                .message("Unread notification count fetched successfully")
                .data(notificationService.getUnreadCount(SecurityUtils.getCurrentUserId()))
                .build());
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable Long notificationId) {

        return ResponseEntity.ok(ApiResponse.<NotificationResponse>builder()
                .success(true)
                .message("Notification marked as read")
                .data(notificationService.markAsRead(
                        SecurityUtils.getCurrentUserId(), notificationId))
                .build());
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead() {
        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .success(true)
                .message("Notifications marked as read")
                .data(notificationService.markAllAsRead(SecurityUtils.getCurrentUserId()))
                .build());
    }
}
