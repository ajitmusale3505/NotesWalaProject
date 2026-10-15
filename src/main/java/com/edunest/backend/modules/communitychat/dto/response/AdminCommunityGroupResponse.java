package com.edunest.backend.modules.communitychat.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCommunityGroupResponse {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private String universityId;
    private String universityName;
    private String universityShortCode;
    private String branchId;
    private String branchName;
    private String branchCode;
    private String semesterId;
    private String semesterName;
    private long memberCount;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
