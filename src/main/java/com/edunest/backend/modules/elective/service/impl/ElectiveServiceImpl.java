package com.edunest.backend.modules.elective.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.elective.dto.*;
import com.edunest.backend.modules.elective.entity.ElectiveGroup;
import com.edunest.backend.modules.elective.repository.*;
import com.edunest.backend.modules.elective.service.ElectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectiveServiceImpl implements ElectiveService {

    private final ElectiveGroupRepository groupRepository;
    private final ElectiveGroupSubjectRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ElectiveGroupResponse> getByCurriculumSemester(String curriculumSemesterId) {
        return groupRepository.findByCurriculumSemesterIdAndActiveTrueOrderByDisplayOrderAsc(
                        curriculumSemesterId)
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ElectiveGroupResponse getById(String id) {
        ElectiveGroup group = groupRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Elective group not found"));
        return map(group);
    }

    private ElectiveGroupResponse map(ElectiveGroup group) {
        List<ElectiveSubjectResponse> subjects = memberRepository
                .findByElectiveGroupIdAndActiveTrueOrderByDisplayOrderAsc(group.getId())
                .stream()
                .filter(member -> member.getSubjectOffering().getCurriculumSemester().getId()
                        .equals(group.getCurriculumSemester().getId()))
                .map(member -> ElectiveSubjectResponse.builder()
                        .membershipId(member.getId())
                        .subjectOfferingId(member.getSubjectOffering().getId())
                        .subjectId(member.getSubjectOffering().getSubject().getBusinessId())
                        .subjectCode(member.getSubjectOffering().getCode())
                        .subjectName(member.getSubjectOffering().getSubject().getName())
                        .credits(member.getSubjectOffering().getCredits())
                        .displayOrder(member.getDisplayOrder())
                        .active(member.isActive())
                        .build())
                .toList();

        if (group.getRequiredSelections() > subjects.size()) {
            throw new BadRequestException(
                    "Elective group configuration is invalid: required selections exceed available subjects");
        }

        return ElectiveGroupResponse.builder()
                .id(group.getId())
                .curriculumSemesterId(group.getCurriculumSemester().getId())
                .code(group.getCode())
                .name(group.getName())
                .description(group.getDescription())
                .requiredSelections(group.getRequiredSelections())
                .availableSubjects(subjects.size())
                .displayOrder(group.getDisplayOrder())
                .active(group.isActive())
                .subjects(subjects)
                .build();
    }
}
