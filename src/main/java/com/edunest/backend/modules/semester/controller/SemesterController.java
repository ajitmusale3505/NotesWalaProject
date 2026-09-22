package com.edunest.backend.modules.semester.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.semester.dto.SemesterResponseDto;
import com.edunest.backend.modules.semester.service.SemesterService;

@RestController
@RequestMapping("/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GetMapping
    public List<SemesterResponseDto> getAllSemesters() {
        return semesterService.getAllSemesters();
    }

    @GetMapping("/{id}")
    public SemesterResponseDto getSemesterById(@PathVariable Long id) {
        return semesterService.getSemesterById(id);
    }

    @GetMapping("/academic-year/{academicYearId}")
    public List<SemesterResponseDto> getSemestersByAcademicYear(
            @PathVariable Long academicYearId) {
        return semesterService.getSemestersByAcademicYearId(academicYearId);
    }
}