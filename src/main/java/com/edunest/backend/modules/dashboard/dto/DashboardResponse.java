package com.edunest.backend.modules.dashboard.dto;

import java.util.List;

import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import com.edunest.backend.modules.subject.dto.SubjectResponseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private Long userId;
    private String userName;
    private boolean profileCompleted;

    private String branchName;
    private String semesterName;

    private Integer subjectCount;
    private Integer resourceCount;

    private List<SubjectResponseDto> subjects;
    private List<ResourceResponse> recentResources;
}