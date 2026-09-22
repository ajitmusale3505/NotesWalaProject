package com.edunest.backend.modules.year.controller;

import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.year.dto.AcademicYearResponse;
import com.edunest.backend.modules.year.service.AcademicYearService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/academic-years")
public class AcademicYearController {
    private final AcademicYearService academicYearService;
    public AcademicYearController(AcademicYearService academicYearService) { this.academicYearService = academicYearService; }

    @GetMapping
    public List<AcademicYearResponse> getAllAcademicYears() {
        return academicYearService.getAllAcademicYears();
    }

    @GetMapping("/{id}")
    public AcademicYearResponse getAcademicYearById(@PathVariable String id) {
        return academicYearService.getAcademicYearById(PublicIdUtils.parseAcademicYearId(id));
    }

    @GetMapping("/university/{universityId}")
    public List<AcademicYearResponse> getAcademicYearsByUniversity(@PathVariable String universityId) {
        return academicYearService.getAcademicYearsByUniversityId(PublicIdUtils.parseUniversityId(universityId));
    }
}