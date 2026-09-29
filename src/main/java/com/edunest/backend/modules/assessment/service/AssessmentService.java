package com.edunest.backend.modules.assessment.service;

import com.edunest.backend.modules.assessment.dto.AssessmentComponentResponse;
import com.edunest.backend.modules.assessment.dto.AssessmentComponentTypeResponse;

import java.util.List;

public interface AssessmentService {
    List<AssessmentComponentTypeResponse> getTypes();
    List<AssessmentComponentResponse> getBySubjectOffering(String subjectOfferingId);
}
