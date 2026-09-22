package com.edunest.backend.modules.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentUserResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private boolean profileCompleted;
}