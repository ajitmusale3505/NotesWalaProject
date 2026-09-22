package com.edunest.backend.modules.year.service;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.year.dto.AcademicYearResponse;
import com.edunest.backend.modules.year.entity.AcademicYear;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;

@Service
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    public AcademicYearService(AcademicYearRepository academicYearRepository) {
        this.academicYearRepository = academicYearRepository;
    }

    public List<AcademicYearResponse> getAllAcademicYears() {
        return academicYearRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AcademicYearResponse getAcademicYearById(Long id) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic Year not found"));

        return mapToResponse(academicYear);
    }

    private AcademicYearResponse mapToResponse(AcademicYear academicYear) {
        return AcademicYearResponse.builder()
                .id(academicYear.getId())
                .name(academicYear.getName())
                .code(academicYear.getCode())
                .startYear(academicYear.getStartYear())
                .endYear(academicYear.getEndYear())
                .active(academicYear.isActive())
                .build();
    }
}