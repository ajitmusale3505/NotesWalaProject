package com.edunest.backend.modules.subject.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.subject.dto.SubjectOfferingResponse;
import com.edunest.backend.modules.subject.entity.SubjectOffering;
import com.edunest.backend.modules.subject.repository.SubjectOfferingRepository;
import com.edunest.backend.modules.subject.service.SubjectOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectOfferingServiceImpl implements SubjectOfferingService {

    private final SubjectOfferingRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectOfferingResponse> getByCurriculumSemester(String curriculumSemesterId) {
        return repository.findByCurriculumSemesterIdAndActiveTrueOrderByCodeAsc(curriculumSemesterId)
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectOfferingResponse getById(String id) {
        return repository.findByIdAndActiveTrue(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Subject offering not found"));
    }

    private SubjectOfferingResponse map(SubjectOffering o) {
        return SubjectOfferingResponse.builder()
                .id(o.getId())
                .subjectId(o.getSubject().getBusinessId())
                .subjectCode(o.getSubject().getCode())
                .subjectName(o.getSubject().getName())
                .curriculumSemesterId(o.getCurriculumSemester().getId())
                .curriculumId(o.getCurriculumSemester().getCurriculum().getId())
                .semesterId(Long.toString(o.getCurriculumSemester().getSemester().getId()))
                .semesterNumber(o.getCurriculumSemester().getSemester().getNumber())
                .code(o.getCode())
                .credits(o.getCredits())
                .mandatory(o.isMandatory())
                .active(o.isActive())
                .build();
    }
}
