package com.edunest.backend.modules.communitychat.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCommunityGroupStatisticsResponse {
    private long totalMembers;
    private long activeMembers;
    private long inactiveMembers;
    private long totalMessages;
    private long suspiciousUsers;
    private double activePercentage;
    private double inactivePercentage;
}
