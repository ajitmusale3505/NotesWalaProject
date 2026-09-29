package com.edunest.backend.modules.assessment.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssessmentComponentTypeResponse {
    String id;
    String code;
    String name;
    String description;
    boolean practical;
    boolean active;
}
