package com.edunest.backend.modules.subject.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubjectOfferingResponse {
    String id;
    String subjectId;
    String subjectCode;
    String subjectName;
    String curriculumSemesterId;
    String curriculumId;
    String semesterId;
    Integer semesterNumber;
    String code;
    String categoryId;
    String categoryCode;
    String categoryName;
    Integer credits;
    boolean mandatory;
    boolean active;
}
