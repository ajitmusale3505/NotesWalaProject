package com.edunest.backend.modules.assessment.dto;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class AssessmentUnitMappingResponse {
    String id;
    String assessmentComponentId;
    String assessmentType;
    String componentKey;
    String unitId;
    Integer unitNumber;
    String chapterName;
    Integer coverageWeight;
    Integer displayOrder;
    boolean active;
}
