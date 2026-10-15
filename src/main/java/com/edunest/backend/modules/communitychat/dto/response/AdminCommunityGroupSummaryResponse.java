package com.edunest.backend.modules.communitychat.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCommunityGroupSummaryResponse {
    private long totalGroups;
    private long activeGroups;
    private long inactiveGroups;
    private long totalMembers;
    private double activePercentage;
    private double inactivePercentage;
}
