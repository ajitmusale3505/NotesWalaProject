package com.edunest.backend.modules.branch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.util.PublicIdUtils;
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
    public BranchResponseDto getBranchById(@PathVariable String id) {
        return branchService.getBranchById(
                PublicIdUtils.parseBranchId(id));
    }

    @GetMapping("/university/{universityId}")
    public List<BranchResponseDto> getBranchesByUniversity(
            @PathVariable String universityId) {
        return branchService.getBranchesByUniversityId(
                PublicIdUtils.parseUniversityId(universityId));
    }

    @GetMapping("/college/{collegeId}")
    public List<BranchResponseDto> getBranchesByCollege(
            @PathVariable String collegeId) {
        return branchService.getBranchesByCollegeId(
                PublicIdUtils.parseCollegeId(collegeId));
    }

    @GetMapping("/academic-year/{academicYearId}")
    public List<BranchResponseDto> getBranchesByAcademicYear(
            @PathVariable String academicYearId) {
        return branchService.getBranchesByAcademicYearId(
                PublicIdUtils.parseAcademicYearId(academicYearId));
    }
}
