package com.edunest.backend.modules.semester.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.semester.dto.SemesterResponseDto;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.semester.repository.SemesterRepository;
import com.edunest.backend.modules.semester.service.SemesterService;

@Service
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;

    public SemesterServiceImpl(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    private SemesterResponseDto mapToDto(Semester semester) {
        return SemesterResponseDto.builder()
                .id(PublicIdUtils.semesterId(semester.getId()))
                .number(semester.getNumber())
                .name(semester.getName())
                .active(semester.isActive())
                .academicYearId(PublicIdUtils.academicYearId(semester.getAcademicYear().getId()))
                .academicYearName(semester.getAcademicYear().getName())
                .build();
    }

    @Override
    public List<SemesterResponseDto> getAllSemesters() {
        return semesterRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SemesterResponseDto getSemesterById(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Semester not found"));

        return mapToDto(semester);
    }

    @Override
    public List<SemesterResponseDto> getSemestersByAcademicYearId(Long academicYearId) {
        return semesterRepository.findByAcademicYearId(academicYearId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
