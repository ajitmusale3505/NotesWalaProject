package com.edunest.backend.modules.academiccontext.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.academiccontext.dto.AcademicContextResponse;
import com.edunest.backend.modules.academiccontext.dto.AcademicSubjectResponse;
import com.edunest.backend.modules.academiccontext.repository.AcademicSubjectResolutionRepository;
import com.edunest.backend.modules.academiccontext.service.AcademicSubjectResolutionService;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicSubjectResolutionServiceImpl implements AcademicSubjectResolutionService {

    private final UserAcademicProfileRepository profileRepository;
    private final AcademicSubjectResolutionRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public AcademicContextResponse getCurrentUserContext() {
        UserAcademicProfile profile = getValidatedProfile();
        var curriculumSemester = subjectRepository.findContext(
                profile.getUniversity().getId(),
                profile.getBranch().getId(),
                profile.getProgram().getId(),
                profile.getExamPattern().getId(),
                profile.getCurrentSemester().getId(),
                resolveStudyYear(profile));

        if (curriculumSemester == null) {
            throw new ResourceNotFoundException("Academic curriculum context not found");
        }

        return AcademicContextResponse.builder()
                .universityId(PublicIdUtils.universityId(profile.getUniversity().getId()))
                .branchId(PublicIdUtils.branchId(profile.getBranch().getId()))
                .programId(profile.getProgram().getId())
                .examPatternId(profile.getExamPattern().getId())
                .academicYearId(PublicIdUtils.academicYearId(profile.getAcademicYear().getId()))
                .semesterId(PublicIdUtils.semesterId(profile.getCurrentSemester().getId()))
                .curriculumId(curriculumSemester.getCurriculum().getId())
                .curriculumSemesterId(curriculumSemester.getId())
                .currentYear(resolveStudyYear(profile))
                .semesterNumber(curriculumSemester.getSemester().getNumber())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicSubjectResponse> getCurrentUserSubjects() {
        UserAcademicProfile profile = getValidatedProfile();

        return subjectRepository.findSubjectsForContext(
                        profile.getUniversity().getId(),
                        profile.getBranch().getId(),
                        profile.getProgram().getId(),
                        profile.getExamPattern().getId(),
                        profile.getCurrentSemester().getId(),
                        profile.getCurrentYear())
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

    private UserAcademicProfile getValidatedProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserAcademicProfile profile = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));

        if (profile.getUniversity() == null || profile.getBranch() == null
                || profile.getAcademicYear() == null || profile.getCurrentSemester() == null) {
            throw new BadRequestException("Complete university, branch, academic year and semester before resolving subjects");
        }
        if (profile.getProgram() == null) {
            throw new BadRequestException("Academic program is required before resolving subjects");
        }
        if (profile.getExamPattern() == null) {
            throw new BadRequestException("Exam pattern is required before resolving subjects");
        }
        Integer studyYear = resolveStudyYear(profile);
        if (studyYear == null || studyYear < 1 || studyYear > 4) {
            throw new BadRequestException("Current academic year must be between 1 and 4 before resolving subjects");
        }
        return profile;
    }

    private Integer resolveStudyYear(UserAcademicProfile profile) {
        String code = profile.getAcademicYear().getCode() == null
                ? "" : profile.getAcademicYear().getCode().toUpperCase();
        if (code.startsWith("SPPU-FE-")) return 1;
        if (code.startsWith("SPPU-SE-")) return 2;
        if (code.startsWith("SPPU-TE-")) return 3;
        if (code.startsWith("SPPU-BE-")) return 4;
        return profile.getCurrentYear();
    }
}
