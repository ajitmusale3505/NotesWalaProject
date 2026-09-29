package com.edunest.backend.modules.curriculum.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CurriculumResponse {
    String id;
    String name;
    String code;
    String description;
    Integer startYear;
    Integer endYear;
    String universityId;
    String universityName;
    String programId;
    String programName;
    String branchId;
    String branchName;
    String examPatternId;
    String examPatternName;
    boolean active;
}
