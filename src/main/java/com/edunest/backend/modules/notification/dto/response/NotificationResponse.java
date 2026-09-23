package com.edunest.backend.modules.notification.dto.response;

import com.edunest.backend.modules.notification.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private String referenceType;
    private Long referenceId;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
