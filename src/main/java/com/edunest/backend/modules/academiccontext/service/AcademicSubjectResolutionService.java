package com.edunest.backend.modules.academiccontext.service;

import com.edunest.backend.modules.academiccontext.dto.AcademicSubjectResponse;
import com.edunest.backend.modules.academiccontext.dto.AcademicContextResponse;
import java.util.List;

public interface AcademicSubjectResolutionService {
    AcademicContextResponse getCurrentUserContext();
    List<AcademicSubjectResponse> getCurrentUserSubjects();
}
