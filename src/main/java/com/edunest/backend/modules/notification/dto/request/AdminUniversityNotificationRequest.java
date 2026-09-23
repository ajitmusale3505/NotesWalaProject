package com.edunest.backend.modules.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUniversityNotificationRequest {

    @NotNull
    private Long universityId;

    @NotBlank
    @Size(max = 160)
    private String title;

    @NotBlank
    @Size(max = 5000)
    private String message;

    private Long referenceId;
}
