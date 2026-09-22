package com.edunest.backend.modules.semester.service;

import java.util.List;

import com.edunest.backend.modules.semester.dto.SemesterResponseDto;

public interface SemesterService {

    List<SemesterResponseDto> getAllSemesters();

    SemesterResponseDto getSemesterById(Long id);

    List<SemesterResponseDto> getSemestersByAcademicYearId(Long academicYearId);
}