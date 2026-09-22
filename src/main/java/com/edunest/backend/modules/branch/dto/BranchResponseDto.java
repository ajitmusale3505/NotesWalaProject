package com.edunest.backend.modules.branch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchResponseDto {

    private Long id;
    private String name;
    private String code;
    private boolean active;

    private Long universityId;
    private String universityName;

    private Long academicYearId;
    private String academicYearName;
}