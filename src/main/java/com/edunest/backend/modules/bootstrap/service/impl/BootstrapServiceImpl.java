package com.edunest.backend.modules.bootstrap.service.impl;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.bootstrap.dto.BootstrapResponse;
import com.edunest.backend.modules.bootstrap.service.BootstrapService;
import com.edunest.backend.modules.branch.service.BranchService;
import com.edunest.backend.modules.college.service.CollegeService;
import com.edunest.backend.modules.semester.service.SemesterService;
import com.edunest.backend.modules.university.service.UniversityService;
import com.edunest.backend.modules.year.service.AcademicYearService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BootstrapServiceImpl implements BootstrapService {

    private final UniversityService universityService;
    private final CollegeService collegeService;
    private final BranchService branchService;
    private final AcademicYearService academicYearService;
    private final SemesterService semesterService;

    @Override
    public BootstrapResponse getBootstrapData() {

        return BootstrapResponse.builder()
                .universities(universityService.getAllUniversities())
                .colleges(collegeService.getAllColleges())
                .branches(branchService.getAllBranches())
                .academicYears(academicYearService.getAllAcademicYears())
                .semesters(semesterService.getAllSemesters())
                .build();
    }
}