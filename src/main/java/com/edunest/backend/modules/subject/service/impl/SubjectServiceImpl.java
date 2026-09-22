package com.edunest.backend.modules.subject.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.subject.dto.SubjectResponseDto;
import com.edunest.backend.modules.subject.entity.Subject;
import com.edunest.backend.modules.subject.repository.SubjectRepository;
import com.edunest.backend.modules.subject.service.SubjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    public SubjectServiceImpl(SubjectRepository subjectRepository) { this.subjectRepository = subjectRepository; }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getAllSubjects() {
        return subjectRepository.findByActiveTrue().stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponseDto getSubjectById(Long id) {
        return subjectRepository.findById(id).filter(Subject::isActive).map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getSubjectsByBranch(Long branchId) {
        return subjectRepository.findByBranchIdAndActiveTrue(branchId).stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getSubjectsBySemester(Long semesterId) {
        return subjectRepository.findBySemesterIdAndActiveTrue(semesterId).stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getSubjectsByBranchAndSemester(Long branchId, Long semesterId) {
        return subjectRepository.findByBranchIdAndSemesterIdAndActiveTrue(branchId, semesterId)
                .stream().map(this::map).toList();
    }

    private SubjectResponseDto map(Subject s) {
        return SubjectResponseDto.builder()
                .id(PublicIdUtils.subjectId(s.getId())).name(s.getName()).code(s.getCode()).active(s.isActive())
                .subjectCategory(s.getSubjectCategory()).examType(s.getExamType())
                .lectureHours(s.getLectureHours()).tutorialHours(s.getTutorialHours()).practicalHours(s.getPracticalHours())
                .inSemMarks(s.getInSemMarks()).endSemMarks(s.getEndSemMarks()).practicalMarks(s.getPracticalMarks())
                .oralMarks(s.getOralMarks()).termWorkMarks(s.getTermWorkMarks()).credits(s.getCredits())
                .elective(s.isElective()).electiveGroup(s.getElectiveGroup()).honors(s.isHonors()).minor(s.isMinor())
                .branchId(PublicIdUtils.branchId(s.getBranch().getId())).branchName(s.getBranch().getName())
                .semesterId(PublicIdUtils.semesterId(s.getSemester().getId())).semesterName(s.getSemester().getName())
                .academicYearId(PublicIdUtils.academicYearId(s.getAcademicYear().getId())).academicYearName(s.getAcademicYear().getName())
                .build();
    }
}