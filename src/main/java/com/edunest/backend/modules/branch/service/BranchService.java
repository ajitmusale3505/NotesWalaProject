package com.edunest.backend.modules.branch.service;

import java.util.List;

import com.edunest.backend.modules.branch.dto.BranchResponseDto;

public interface BranchService {

    List<BranchResponseDto> getAllBranches();

    BranchResponseDto getBranchById(Long id);

    List<BranchResponseDto> getBranchesByUniversityId(Long universityId);

    List<BranchResponseDto> getBranchesByCollegeId(Long collegeId);

    List<BranchResponseDto> getBranchesByAcademicYearId(Long academicYearId);
}