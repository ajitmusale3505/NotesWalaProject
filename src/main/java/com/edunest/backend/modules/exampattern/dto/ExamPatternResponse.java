package com.edunest.backend.modules.exampattern.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExamPatternResponse {
    String id;
    String name;
    String code;
    String description;
    Integer effectiveFromYear;
    Integer effectiveToYear;
    String universityId;
    String universityName;
    boolean active;
}
