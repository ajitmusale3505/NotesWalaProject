package com.edunest.backend.modules.curriculum.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.curriculum.dto.*;
import com.edunest.backend.modules.curriculum.entity.*;
import com.edunest.backend.modules.curriculum.repository.*;
import com.edunest.backend.modules.curriculum.service.CurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final CurriculumSemesterRepository curriculumSemesterRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getAllActive() {
        return curriculumRepository.findAllByActiveTrueOrderByStartYearDescNameAsc()
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CurriculumResponse getById(String id) {
        return curriculumRepository.findByIdAndActiveTrue(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getByBranchId(String branchId) {
        Long legacyBranchId = PublicIdUtils.parseBranchId(branchId);
        return curriculumRepository.findByBranchIdAndActiveTrueOrderByStartYearDesc(legacyBranchId)
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumSemesterResponse> getSemesters(String curriculumId) {
        if (!curriculumRepository.existsById(curriculumId)) {
            throw new ResourceNotFoundException("Curriculum not found");
        }
        return curriculumSemesterRepository
                .findByCurriculumIdAndActiveTrueOrderByStudyYearAscDisplayOrderAsc(curriculumId)
                .stream().map(this::mapSemester).toList();
    }

    private CurriculumResponse map(Curriculum c) {
        return CurriculumResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .code(c.getCode())
                .description(c.getDescription())
                .startYear(c.getStartYear())
                .endYear(c.getEndYear())
                .universityId(PublicIdUtils.universityId(c.getUniversity().getId()))
                .universityName(c.getUniversity().getName())
                .programId(c.getProgram().getId())
                .programName(c.getProgram().getName())
                .branchId(PublicIdUtils.branchId(c.getBranch().getId()))
                .branchName(c.getBranch().getName())
                .examPatternId(c.getExamPattern().getId())
                .examPatternName(c.getExamPattern().getName())
                .active(c.isActive())
                .build();
    }

    private CurriculumSemesterResponse mapSemester(CurriculumSemester cs) {
        return CurriculumSemesterResponse.builder()
                .id(cs.getId())
                .curriculumId(cs.getCurriculum().getId())
                .semesterId(PublicIdUtils.semesterId(cs.getSemester().getId()))
                .semesterNumber(cs.getSemester().getNumber())
                .semesterName(cs.getSemester().getName())
                .studyYear(cs.getStudyYear())
                .displayOrder(cs.getDisplayOrder())
                .active(cs.isActive())
                .build();
    }
}
