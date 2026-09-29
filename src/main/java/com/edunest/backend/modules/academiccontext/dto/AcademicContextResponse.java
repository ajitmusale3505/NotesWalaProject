package com.edunest.backend.modules.academiccontext.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AcademicContextResponse {
    String universityId;
    String branchId;
    String programId;
    String examPatternId;
    String academicYearId;
    String semesterId;
    String curriculumId;
    String curriculumSemesterId;
    Integer currentYear;
    Integer semesterNumber;
}
