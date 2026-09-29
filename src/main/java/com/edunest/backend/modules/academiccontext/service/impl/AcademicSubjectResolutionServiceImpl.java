package com.edunest.backend.modules.academiccontext.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.academiccontext.dto.AcademicSubjectResponse;
import com.edunest.backend.modules.academiccontext.dto.AcademicContextResponse;
import com.edunest.backend.modules.academiccontext.repository.AcademicSubjectResolutionRepository;
import com.edunest.backend.modules.academiccontext.service.AcademicSubjectResolutionService;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor
public class AcademicSubjectResolutionServiceImpl implements AcademicSubjectResolutionService {

    private final UserAcademicProfileRepository profileRepository;
    private final AcademicSubjectResolutionRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public AcademicContextResponse getCurrentUserContext() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserAcademicProfile p = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));
        validateProfileContext(p);
        var curriculum = subjectRepository.findContext(
                p.getUniversity().getId(), p.getBranch().getId(), p.getProgram().getId(),
                p.getExamPattern().getId(), p.getCurrentSemester().getId(), p.getCurrentYear());
        if (curriculum == null) throw new ResourceNotFoundException("Academic curriculum context not found");
        return AcademicContextResponse.builder()
                .universityId(PublicIdUtils.universityId(p.getUniversity().getId()))
                .branchId(PublicIdUtils.branchId(p.getBranch().getId()))
                .programId(p.getProgram().getId())
                .examPatternId(p.getExamPattern().getId())
                .academicYearId(PublicIdUtils.academicYearId(p.getAcademicYear().getId()))
                .semesterId(PublicIdUtils.semesterId(p.getCurrentSemester().getId()))
                .curriculumId(curriculum.getCurriculum().getId())
                .curriculumSemesterId(curriculum.getId())
                .currentYear(p.getCurrentYear())
                .semesterNumber(curriculum.getSemester().getNumber())
                .build();
        private void validateProfileContext(UserAcademicProfile p) {
        if (p.getProgram() == null) throw new BadRequestException("Academic program is required before resolving subjects");
        if (p.getExamPattern() == null) throw new BadRequestException("Exam pattern is required before resolving subjects");
        if (p.getCurrentYear() == null || p.getCurrentYear() < 1 || p.getCurrentYear() > 4)
            throw new BadRequestException("Current academic year must be between 1 and 4 before resolving subjects");
    }

}

    @Override
    @Transactional(readOnly = true)
    public List<AcademicSubjectResponse> getCurrentUserSubjects() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserAcademicProfile p = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));

        validate

        return subjectRepository.findSubjectsForContext(
                        p.getUniversity().getId(),
                        p.getBranch().getId(),
                        p.getProgram().getId(),
                        p.getExamPattern().getId(),
                        p.getCurrentSemester().getId(),
                        p.getCurrentYear())
                .stream()
                .map(so -> AcademicSubjectResponse.builder()
                        .subjectOfferingId(so.getId())
                        .subjectId(so.getSubject().getBusinessId())
                        .subjectCode(so.getCode())
                        .subjectName(so.getSubject().getName())
                        .categoryId(so.getCategory().getId())
                        .categoryCode(so.getCategory().getCode())
                        .categoryName(so.getCategory().getName())
                        .credits(so.getCredits())
                        .mandatory(so.isMandatory())
                        .universityId(PublicIdUtils.universityId(so.getCurriculumSemester().getCurriculum().getUniversity().getId()))
                        .branchId(PublicIdUtils.branchId(so.getCurriculumSemester().getCurriculum().getBranch().getId()))
                        .examPatternId(so.getCurriculumSemester().getCurriculum().getExamPattern().getId())
                        .curriculumId(so.getCurriculumSemester().getCurriculum().getId())
                        .curriculumSemesterId(so.getCurriculumSemester().getId())
                        .semesterNumber(so.getCurriculumSemester().getSemester().getNumber())
                        .studyYear(so.getCurriculumSemester().getStudyYear())
                        .build())
                .toList();
    }
}
