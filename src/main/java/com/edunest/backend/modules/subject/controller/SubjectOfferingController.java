package com.edunest.backend.modules.subject.controller;

import com.edunest.backend.modules.subject.dto.SubjectOfferingResponse;
import com.edunest.backend.modules.subject.service.SubjectOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject-offerings")
@RequiredArgsConstructor
public class SubjectOfferingController {

    private final SubjectOfferingService service;

    @GetMapping("/curriculum-semester/{curriculumSemesterId}")
    public List<SubjectOfferingResponse> getByCurriculumSemester(
            @PathVariable String curriculumSemesterId) {
        return service.getByCurriculumSemester(curriculumSemesterId);
    }

    @GetMapping("/{id}")
    public SubjectOfferingResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}
