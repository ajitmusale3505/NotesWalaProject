package com.edunest.backend.modules.admin.dto;

import com.edunest.backend.common.enums.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserUpdateRequest {
    @NotNull
    private RoleType role;
    @NotNull
    private Boolean enabled;
}