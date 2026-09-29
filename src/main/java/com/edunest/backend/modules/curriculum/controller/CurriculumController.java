package com.edunest.backend.modules.curriculum.controller;

import com.edunest.backend.modules.curriculum.dto.*;
import com.edunest.backend.modules.curriculum.service.CurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curriculums")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService service;

    @GetMapping
    public List<CurriculumResponse> getAll() {
        return service.getAllActive();
    }

    @GetMapping("/{id}")
    public CurriculumResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/branch/{branchId}")
    public List<CurriculumResponse> getByBranch(@PathVariable String branchId) {
        return service.getByBranchId(branchId);
    }

    @GetMapping("/{curriculumId}/semesters")
    public List<CurriculumSemesterResponse> getSemesters(
            @PathVariable String curriculumId) {
        return service.getSemesters(curriculumId);
    }
}
