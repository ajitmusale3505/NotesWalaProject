package com.edunest.backend.modules.assessment.controller;

import com.edunest.backend.modules.assessment.dto.*;
import com.edunest.backend.modules.assessment.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService service;

    @GetMapping("/types")
    public List<AssessmentComponentTypeResponse> getTypes() {
        return service.getTypes();
    }

    @GetMapping("/subject-offering/{subjectOfferingId}")
    public List<AssessmentComponentResponse> getBySubjectOffering(
            @PathVariable String subjectOfferingId) {
        return service.getBySubjectOffering(subjectOfferingId);
    }
}
