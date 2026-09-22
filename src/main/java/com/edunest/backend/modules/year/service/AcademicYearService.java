package com.edunest.backend.modules.year.service;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.year.dto.AcademicYearResponse;
import com.edunest.backend.modules.year.entity.AcademicYear;
import com.edunest.backend.modules.year.repository.AcademicYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AcademicYearService {
    private final AcademicYearRepository academicYearRepository;
    public AcademicYearService(AcademicYearRepository academicYearRepository) {
        this.academicYearRepository = academicYearRepository;
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponse> getAllAcademicYears() {
        return academicYearRepository.findAllByActiveTrue().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public AcademicYearResponse getAcademicYearById(Long id) {
        return academicYearRepository.findByIdAndActiveTrue(id).map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Academic Year not found"));
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponse> getAcademicYearsByUniversityId(Long universityId) {
        return academicYearRepository.findByUniversityIdAndActiveTrue(universityId)
                .stream().map(this::mapToResponse).toList();
    }

    private AcademicYearResponse mapToResponse(AcademicYear year) {
        return AcademicYearResponse.builder()
                .id(PublicIdUtils.academicYearId(year.getId()))
                .name(year.getName()).code(year.getCode())
                .startYear(year.getStartYear()).endYear(year.getEndYear())
                .active(year.isActive())
                .universityId(year.getUniversity() == null ? null : PublicIdUtils.universityId(year.getUniversity().getId()))
                .universityName(year.getUniversity() == null ? null : year.getUniversity().getName())
                .build();
    }
}