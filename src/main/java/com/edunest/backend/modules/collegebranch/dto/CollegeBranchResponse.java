package com.edunest.backend.modules.collegebranch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeBranchResponse {

    private Long id;

    private Long collegeId;
    private String collegeName;

    private Long branchId;
    private String branchName;
    private String branchCode;

    private boolean active;
}