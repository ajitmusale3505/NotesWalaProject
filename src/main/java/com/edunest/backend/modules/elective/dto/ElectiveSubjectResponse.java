package com.edunest.backend.modules.elective.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ElectiveSubjectResponse {
    String membershipId;
    String subjectOfferingId;
    String subjectId;
    String subjectCode;
    String subjectName;
    Integer credits;
    Integer displayOrder;
    boolean active;
}
