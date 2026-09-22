package com.edunest.backend.modules.collegebranch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeBranchResponse {

    private String id;

    private String collegeId;
    private String collegeName;

    private String branchId;
    private String branchName;
    private String branchCode;

    private boolean active;
}
