package com.edunest.backend.modules.bootstrap.dto;

import java.util.List;

import com.edunest.backend.modules.branch.dto.BranchResponseDto;
import com.edunest.backend.modules.college.dto.response.CollegeResponse;
import com.edunest.backend.modules.semester.dto.SemesterResponseDto;
import com.edunest.backend.modules.university.dto.response.UniversityResponse;
import com.edunest.backend.modules.year.dto.AcademicYearResponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootstrapResponse {

    private List<UniversityResponse> universities;
    private List<CollegeResponse> colleges;
    private List<BranchResponseDto> branches;
    private List<AcademicYearResponse> academicYears;
    private List<SemesterResponseDto> semesters;
}