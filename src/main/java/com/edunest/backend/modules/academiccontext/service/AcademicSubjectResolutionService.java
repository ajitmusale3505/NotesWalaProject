package com.edunest.backend.modules.academiccontext.service;

import com.edunest.backend.modules.academiccontext.dto.AcademicSubjectResponse;
import java.util.List;

public interface AcademicSubjectResolutionService {
    List<AcademicSubjectResponse> getCurrentUserSubjects();
}
