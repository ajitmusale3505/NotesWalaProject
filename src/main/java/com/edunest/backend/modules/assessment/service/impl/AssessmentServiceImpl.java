package com.edunest.backend.modules.assessment.service.impl;

import com.edunest.backend.modules.assessment.dto.*;
import com.edunest.backend.modules.assessment.repository.*;
import com.edunest.backend.modules.assessment.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentComponentTypeRepository typeRepository;
    private final AssessmentComponentRepository componentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentComponentTypeResponse> getTypes() {
        return typeRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(t -> AssessmentComponentTypeResponse.builder()
                        .id(t.getId())
                        .code(t.getCode())
                        .name(t.getName())
                        .description(t.getDescription())
                        .practical(t.isPractical())
                        .active(t.isActive())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentComponentResponse> getBySubjectOffering(String subjectOfferingId) {
        return componentRepository
                .findBySubjectOfferingIdAndActiveTrueOrderByDisplayOrderAsc(subjectOfferingId)
                .stream()
                .map(c -> AssessmentComponentResponse.builder()
                        .id(c.getId())
                        .subjectOfferingId(c.getSubjectOffering().getId())
                        .typeId(c.getType().getId())
                        .typeCode(c.getType().getCode())
                        .typeName(c.getType().getName())
                        .componentKey(c.getComponentKey())
                        .maxMarks(c.getMaxMarks())
                        .passingMarks(c.getPassingMarks())
                        .displayOrder(c.getDisplayOrder())
                        .includedInTotal(c.isIncludedInTotal())
                        .active(c.isActive())
                        .build())
                .toList();
    }
}
