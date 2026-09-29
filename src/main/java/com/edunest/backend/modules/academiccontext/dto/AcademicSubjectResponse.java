package com.edunest.backend.modules.academiccontext.dto;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class AcademicSubjectResponse {
    String subjectOfferingId;
    String subjectId;
    String subjectCode;
    String subjectName;
    String categoryId;
    String categoryCode;
    String categoryName;
    Integer credits;
    boolean mandatory;
    String universityId;
    String branchId;
    String examPatternId;
    String curriculumId;
    String curriculumSemesterId;
    Integer semesterNumber;
    Integer studyYear;
}
