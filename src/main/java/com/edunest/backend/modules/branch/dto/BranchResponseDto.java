package com.edunest.backend.modules.branch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchResponseDto {

    private String id;
    private String name;
    private String code;
    private boolean active;

    private String universityId;
    private String universityName;

    private String academicYearId;
    private String academicYearName;
}
