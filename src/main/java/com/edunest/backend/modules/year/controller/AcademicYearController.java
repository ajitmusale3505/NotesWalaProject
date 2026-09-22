package com.edunest.backend.modules.year.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.year.dto.AcademicYearResponse;
import com.edunest.backend.modules.year.service.AcademicYearService;

@RestController
@RequestMapping("/academic-years")
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    public AcademicYearController(AcademicYearService academicYearService) {
        this.academicYearService = academicYearService;
    }

    @GetMapping
    public List<AcademicYearResponse> getAllAcademicYears() {
        return academicYearService.getAllAcademicYears();
    }

    @GetMapping("/{id}")
    public AcademicYearResponse getAcademicYearById(@PathVariable Long id) {
        return academicYearService.getAcademicYearById(id);
    }
}