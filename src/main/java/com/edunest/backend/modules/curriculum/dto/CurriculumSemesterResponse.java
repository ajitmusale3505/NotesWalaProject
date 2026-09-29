package com.edunest.backend.modules.curriculum.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CurriculumSemesterResponse {
    String id;
    String curriculumId;
    String semesterId;
    Integer semesterNumber;
    String semesterName;
    Integer studyYear;
    Integer displayOrder;
    boolean active;
}
