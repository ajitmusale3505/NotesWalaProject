package com.edunest.backend.modules.assessment.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssessmentComponentResponse {
    String id;
    String subjectOfferingId;
    String typeId;
    String typeCode;
    String typeName;
    String componentKey;
    Integer maxMarks;
    Integer passingMarks;
    Integer displayOrder;
    boolean includedInTotal;
    boolean active;
}
