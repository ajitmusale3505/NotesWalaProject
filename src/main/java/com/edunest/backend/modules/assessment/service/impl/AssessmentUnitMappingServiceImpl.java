package com.edunest.backend.modules.assessment.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.id.BusinessIdGenerator;
import com.edunest.backend.modules.assessment.dto.AssessmentUnitMappingResponse;
import com.edunest.backend.modules.assessment.entity.AssessmentUnitMapping;
import com.edunest.backend.modules.assessment.repository.AssessmentUnitMappingRepository;
import com.edunest.backend.modules.assessment.repository.AssessmentComponentRepository;
import com.edunest.backend.modules.unit.repository.UnitRepository;
import com.edunest.backend.modules.assessment.service.AssessmentUnitMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor
public class AssessmentUnitMappingServiceImpl implements AssessmentUnitMappingService {
    private final AssessmentUnitMappingRepository repository;
    private final AssessmentComponentRepository componentRepository;
    private final UnitRepository unitRepository;
    private final BusinessIdGenerator businessIdGenerator;


    @Transactional
    public AssessmentUnitMappingResponse create(String assessmentComponentId, Long unitId, Integer coverageWeight, Integer displayOrder) {
        var component = componentRepository.findById(assessmentComponentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment component not found"));
        var unit = unitRepository.findByIdAndActiveTrue(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        if (unit.getSubjectOffering() == null || !component.getSubjectOffering().getId().equals(unit.getSubjectOffering().getId())) {
            throw new BadRequestException("Assessment component and unit must belong to the same subject offering");
        }
        if (repository.existsByAssessmentComponentIdAndUnitId(assessmentComponentId, unitId)) {
            throw new BadRequestException("Assessment component is already mapped to this unit");
        }
        var mapping = AssessmentUnitMapping.builder().id(businessIdGenerator.nextId("AUM"))
                .assessmentComponent(component).unit(unit).coverageWeight(coverageWeight == null ? 100 : coverageWeight)
                .displayOrder(displayOrder == null ? 1 : displayOrder).active(true).build();
        return map(repository.save(mapping));
    }

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
