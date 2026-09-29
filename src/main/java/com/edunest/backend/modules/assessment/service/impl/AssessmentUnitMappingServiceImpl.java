package com.edunest.backend.modules.assessment.service.impl;

import com.edunest.backend.modules.assessment.dto.AssessmentUnitMappingResponse;
import com.edunest.backend.modules.assessment.entity.AssessmentUnitMapping;
import com.edunest.backend.modules.assessment.repository.AssessmentUnitMappingRepository;
import com.edunest.backend.modules.assessment.service.AssessmentUnitMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor
public class AssessmentUnitMappingServiceImpl implements AssessmentUnitMappingService {
    private final AssessmentUnitMappingRepository repository;

    @Override @Transactional(readOnly = true)
    public List<AssessmentUnitMappingResponse> getByAssessmentComponent(String id) {
        return repository.findByAssessmentComponentIdAndActiveTrueOrderByDisplayOrderAsc(id).stream().map(this::map).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<AssessmentUnitMappingResponse> getByUnit(Long unitId) {
        return repository.findByUnitIdAndActiveTrueOrderByDisplayOrderAsc(unitId).stream().map(this::map).toList();
    }

    private AssessmentUnitMappingResponse map(AssessmentUnitMapping m) {
        return AssessmentUnitMappingResponse.builder()
                .id(m.getId())
                .assessmentComponentId(m.getAssessmentComponent().getId())
                .assessmentType(m.getAssessmentComponent().getType().getCode())
                .componentKey(m.getAssessmentComponent().getComponentKey())
                .unitId(m.getUnit().getBusinessId())
                .unitNumber(m.getUnit().getUnitNumber())
                .chapterName(m.getUnit().getChapterName())
                .coverageWeight(m.getCoverageWeight())
                .displayOrder(m.getDisplayOrder())
                .active(m.isActive())
                .build();
    }
}
