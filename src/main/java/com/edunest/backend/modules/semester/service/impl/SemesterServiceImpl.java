package com.edunest.backend.modules.semester.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.semester.dto.SemesterResponseDto;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.semester.repository.SemesterRepository;
import com.edunest.backend.modules.semester.service.SemesterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SemesterServiceImpl implements SemesterService {
    private final SemesterRepository semesterRepository;
    public SemesterServiceImpl(SemesterRepository semesterRepository) { this.semesterRepository = semesterRepository; }

    @Override
    @Transactional(readOnly = true)
    public List<SemesterResponseDto> getAllSemesters() {
        return semesterRepository.findAllByActiveTrue().stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SemesterResponseDto getSemesterById(Long id) {
        return semesterRepository.findByIdAndActiveTrue(id).map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SemesterResponseDto> getSemestersByAcademicYearId(Long academicYearId) {
        return semesterRepository.findByAcademicYearIdAndActiveTrue(academicYearId)
                .stream().map(this::mapToDto).toList();
    }

    private SemesterResponseDto mapToDto(Semester semester) {
        return SemesterResponseDto.builder()
                .id(PublicIdUtils.semesterId(semester.getId()))
                .number(semester.getNumber()).name(semester.getName()).active(semester.isActive())
                .academicYearId(PublicIdUtils.academicYearId(semester.getAcademicYear().getId()))
                .academicYearName(semester.getAcademicYear().getName())
                .build();
    }
}