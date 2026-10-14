package com.edunest.backend.modules.communitychat.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityRoomResponse {
    private Long id;
    private Long universityId;
    private String universityName;
    private long memberCount;
    private String branchName;
    private String semesterName;
    private Integer currentYear;
    private boolean active;
}
