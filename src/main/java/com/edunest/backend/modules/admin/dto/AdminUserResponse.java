package com.edunest.backend.modules.admin.dto;

import java.time.LocalDateTime;
import com.edunest.backend.common.enums.RoleType;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String fullName;
    private String email;
    private RoleType role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}