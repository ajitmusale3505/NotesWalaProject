package com.edunest.backend.modules.assessment.service;

import com.edunest.backend.modules.assessment.dto.AssessmentUnitMappingResponse;
import java.util.List;

public interface AssessmentUnitMappingService {
    AssessmentUnitMappingResponse create(String assessmentComponentId, Long unitId, Integer coverageWeight, Integer displayOrder);
    List<AssessmentUnitMappingResponse> getByAssessmentComponent(String assessmentComponentId);
    List<AssessmentUnitMappingResponse> getByUnit(Long unitId);
}
