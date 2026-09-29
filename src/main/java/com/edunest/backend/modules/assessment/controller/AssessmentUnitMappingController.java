package com.edunest.backend.modules.assessment.controller;

import com.edunest.backend.modules.assessment.dto.AssessmentUnitMappingResponse;
import com.edunest.backend.modules.assessment.service.AssessmentUnitMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/assessment-unit-mappings") @RequiredArgsConstructor
public class AssessmentUnitMappingController {
    private final AssessmentUnitMappingService service;

    @PostMapping("/assessment-component/{assessmentComponentId}/unit/{unitId}")
    public AssessmentUnitMappingResponse create(
            @PathVariable String assessmentComponentId,
            @PathVariable Long unitId,
            @RequestParam(required = false) Integer coverageWeight,
            @RequestParam(required = false) Integer displayOrder) {
        return service.create(assessmentComponentId, unitId, coverageWeight, displayOrder);
    }

    @GetMapping("/assessment-component/{assessmentComponentId}")
    public List<AssessmentUnitMappingResponse> getByAssessmentComponent(@PathVariable String assessmentComponentId) {
        return service.getByAssessmentComponent(assessmentComponentId);
    }

    @GetMapping("/unit/{unitId}")
    public List<AssessmentUnitMappingResponse> getByUnit(@PathVariable Long unitId) {
        return service.getByUnit(unitId);
    }
}
