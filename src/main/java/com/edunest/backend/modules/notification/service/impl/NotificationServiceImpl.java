package com.edunest.backend.modules.notification.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.notification.dto.response.NotificationResponse;
import com.edunest.backend.modules.notification.dto.response.UnreadCountResponse;
import com.edunest.backend.modules.notification.entity.Notification;
import com.edunest.backend.modules.notification.entity.NotificationType;
import com.edunest.backend.modules.notification.repository.NotificationRepository;
import com.edunest.backend.modules.notification.service.NotificationService;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final int MAX_PAGE_SIZE = 50;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserAcademicProfileRepository profileRepository;

    @Override
    public Page<NotificationResponse> getMyNotifications(Long userId, int page, int size) {
        requireUser(userId);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        return notificationRepository.findByUser_Id(
                        userId,
                        PageRequest.of(
                                safePage,
                                safeSize,
                                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))))
                .map(this::toResponse);
    }

    @Override
    public UnreadCountResponse getUnreadCount(Long userId) {
        requireUser(userId);
        return UnreadCountResponse.builder()
                .unreadCount(notificationRepository.countByUser_IdAndReadAtIsNull(userId))
                .build();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        requireUser(userId);

        Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return toResponse(notification);
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        requireUser(userId);
        return notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void createPurchaseNotification(Long userId, Long resourceId, String resourceTitle) {
        createForUser(
                userId,
                NotificationType.PURCHASE,
                "Purchase successful",
                "Your purchase of \"" + safeTitle(resourceTitle) + "\" was completed successfully.",
                "RESOURCE",
                resourceId);
    }

    @Override
    @Transactional
    public void createSubscriptionNotification(Long userId, Long subscriptionId, String planName) {
        createForUser(
                userId,
                NotificationType.SUBSCRIPTION,
                "Subscription activated",
                "Your \"" + safeTitle(planName) + "\" subscription is now active.",
                "SUBSCRIPTION",
                subscriptionId);
    }

    @Override
    @Transactional
    public void createNewResourceNotification(Long resourceId, Long universityId, String resourceTitle) {
        if (universityId == null) {
            return;
        }

        List<Long> userIds = profileRepository.findUserIdsByUniversityId(universityId);
        if (userIds.isEmpty()) {
            return;
        }

        List<User> users = userRepository.findAllById(userIds);
        List<Notification> notifications = users.stream()
                .map(user -> build(
                        user,
                        NotificationType.NEW_RESOURCE,
                        "New resource available",
                        "\"" + safeTitle(resourceTitle) + "\" is now available for your university.",
                        "RESOURCE",
                        resourceId))
                .toList();

        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void createUniversityUpdateNotification(
            Long universityId,
            String title,
            String message,
            Long referenceId) {

        if (universityId == null) {
            throw new BadRequestException("University is required");
        }

        List<Long> userIds = profileRepository.findUserIdsByUniversityId(universityId);
        List<User> users = userRepository.findAllById(userIds);

        notificationRepository.saveAll(users.stream()
                .map(user -> build(
                        user,
                        NotificationType.UNIVERSITY_UPDATE,
                        validateTitle(title),
                        validateMessage(message),
                        "UNIVERSITY",
                        referenceId))
                .toList());
    }

    @Override
    @Transactional
    public void createSystemNotification(String title, String message, Long referenceId) {
        List<User> users = userRepository.findAll();

        notificationRepository.saveAll(users.stream()
                .map(user -> build(
                        user,
                        NotificationType.SYSTEM,
                        validateTitle(title),
                        validateMessage(message),
                        "SYSTEM",
                        referenceId))
                .toList());
    }

    private void createForUser(
            Long userId,
            NotificationType type,
            String title,
            String message,
            String referenceType,
            Long referenceId) {

        User user = requireUser(userId);
        notificationRepository.save(
                build(user, type, title, message, referenceType, referenceId));
    }

    private Notification build(
            User user,
            NotificationType type,
            String title,
            String message,
            String referenceType,
            Long referenceId) {

        return Notification.builder()
                .user(user)
                .type(type)
                .title(validateTitle(title))
                .message(validateMessage(message))
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();
    }

    private User requireUser(Long userId) {
        if (userId == null) {
            throw new AccessDeniedException("Authenticated user required");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .read(notification.getReadAt() != null)
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }

    private String safeTitle(String value) {
        return value == null || value.isBlank() ? "resource" : value.trim();
    }

    private String validateTitle(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Notification title is required");
        }
        String normalized = value.trim();
        if (normalized.length() > 160) {
            throw new BadRequestException("Notification title must be at most 160 characters");
        }
        return normalized;
    }

    private String validateMessage(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Notification message is required");
        }
        String normalized = value.trim();
        if (normalized.length() > 5000) {
            throw new BadRequestException("Notification message must be at most 5000 characters");
        }
        return normalized;
    }
}
