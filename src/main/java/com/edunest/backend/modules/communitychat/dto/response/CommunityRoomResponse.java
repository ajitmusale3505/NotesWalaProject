package com.edunest.backend.modules.communitychat.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityRoomResponse {
    private Long id;
    private String groupType;
    private String displayName;
    private String description;
    private String iconUrl;
    private Long universityId;
    private String universityName;
    private Long collegeId;
    private String collegeName;
    private Long branchId;
    private String branchName;
    private long memberCount;
    private String semesterName;
    private Integer currentYear;
    private boolean active;
}
