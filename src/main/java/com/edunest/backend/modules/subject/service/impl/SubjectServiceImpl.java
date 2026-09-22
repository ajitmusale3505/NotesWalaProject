package com.edunest.backend.modules.subject.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.subject.dto.SubjectResponseDto;
import com.edunest.backend.modules.subject.entity.Subject;
import com.edunest.backend.modules.subject.repository.SubjectRepository;
import com.edunest.backend.modules.subject.service.SubjectService;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    private SubjectResponseDto map(Subject s) {
        return SubjectResponseDto.builder()
                .id(s.getId())
                .name(s.getName())
                .code(s.getCode())
                .active(s.isActive())
                .subjectCategory(s.getSubjectCategory())
                .examType(s.getExamType())
                .credits(s.getCredits())
                .branchId(s.getBranch().getId())
                .branchName(s.getBranch().getName())
                .semesterId(s.getSemester().getId())
                .semesterName(s.getSemester().getName())
                .academicYearId(s.getAcademicYear().getId())
                .academicYearName(s.getAcademicYear().getName())
                .build();
    }

    @Override
    public List<SubjectResponseDto> getAllSubjects() {
        return subjectRepository.findByActiveTrue()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectResponseDto getSubjectById(Long id) {
        return map(subjectRepository.findById(id).orElseThrow());
    }

    @Override
    public List<SubjectResponseDto> getSubjectsByBranch(Long branchId) {
        return subjectRepository.findByBranchIdAndActiveTrue(branchId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubjectResponseDto> getSubjectsBySemester(Long semesterId) {
        return subjectRepository.findBySemesterIdAndActiveTrue(semesterId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    @Override
    public List<SubjectResponseDto> getSubjectsByBranchAndSemester(
            Long branchId,
            Long semesterId) {

        return subjectRepository
                .findByBranchIdAndSemesterIdAndActiveTrue(
                        branchId,
                        semesterId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }
}