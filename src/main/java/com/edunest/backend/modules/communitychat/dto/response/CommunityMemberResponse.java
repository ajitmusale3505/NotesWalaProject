package com.edunest.backend.modules.communitychat.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityMemberResponse {
    private Long userId;
    private String fullName;
    private String branchName;
    private String semesterName;
    private Integer currentYear;
}
