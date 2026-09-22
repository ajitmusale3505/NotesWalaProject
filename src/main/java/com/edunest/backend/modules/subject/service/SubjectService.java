package com.edunest.backend.modules.subject.service;

import java.util.List;
import com.edunest.backend.modules.subject.dto.SubjectResponseDto;

public interface SubjectService {

    List<SubjectResponseDto> getAllSubjects();

    SubjectResponseDto getSubjectById(Long id);

    List<SubjectResponseDto> getSubjectsByBranch(Long branchId);

    List<SubjectResponseDto> getSubjectsBySemester(Long semesterId);

    List<SubjectResponseDto> getSubjectsByBranchAndSemester(
            Long branchId,
            Long semesterId);
}