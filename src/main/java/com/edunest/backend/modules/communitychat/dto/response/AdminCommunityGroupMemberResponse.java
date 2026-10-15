package com.edunest.backend.modules.communitychat.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCommunityGroupMemberResponse {
    private Long userId;
    private String fullName;
    private String rollNumber;
    private String email;
    private String phoneNumber;
    private String semesterName;
    private String branchName;
    private Integer currentYear;
    private long messageCount;
    private long suspiciousActivityCount;
    private boolean active;
    private LocalDateTime joinedAt;
}
