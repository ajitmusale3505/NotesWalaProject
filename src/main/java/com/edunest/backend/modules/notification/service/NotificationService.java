package com.edunest.backend.modules.notification.service;

import com.edunest.backend.modules.notification.dto.response.NotificationResponse;
import com.edunest.backend.modules.notification.dto.response.UnreadCountResponse;
import org.springframework.data.domain.Page;

public interface NotificationService {

    Page<NotificationResponse> getMyNotifications(Long userId, int page, int size);

    UnreadCountResponse getUnreadCount(Long userId);

    NotificationResponse markAsRead(Long userId, Long notificationId);

    int markAllAsRead(Long userId);

    void createPurchaseNotification(Long userId, Long resourceId, String resourceTitle);

    void createSubscriptionNotification(Long userId, Long subscriptionId, String planName);

    void createNewResourceNotification(Long resourceId, Long universityId, String resourceTitle);

    void createUniversityUpdateNotification(Long universityId, String title, String message, Long referenceId);

    void createSystemNotification(String title, String message, Long referenceId);
}
