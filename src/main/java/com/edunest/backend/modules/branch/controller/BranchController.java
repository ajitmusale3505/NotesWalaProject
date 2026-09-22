package com.edunest.backend.modules.branch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.branch.dto.BranchResponseDto;
import com.edunest.backend.modules.branch.service.BranchService;

@RestController
@RequestMapping("/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public List<BranchResponseDto> getAllBranches() {
        return branchService.getAllBranches();
    }

    @GetMapping("/{id}")
    public BranchResponseDto getBranchById(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }

    @GetMapping("/university/{universityId}")
    public List<BranchResponseDto> getBranchesByUniversity(
            @PathVariable Long universityId) {
        return branchService.getBranchesByUniversityId(universityId);
    }
    
    @GetMapping("/college/{collegeId}")
    public List<BranchResponseDto> getBranchesByCollege(
            @PathVariable Long collegeId) {
        return branchService.getBranchesByCollegeId(collegeId);
    }

    @GetMapping("/academic-year/{academicYearId}")
    public List<BranchResponseDto> getBranchesByAcademicYear(
            @PathVariable Long academicYearId) {
        return branchService.getBranchesByAcademicYearId(academicYearId);
    }
}