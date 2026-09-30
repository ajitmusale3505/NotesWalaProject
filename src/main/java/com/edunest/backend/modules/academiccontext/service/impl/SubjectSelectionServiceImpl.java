package com.edunest.backend.modules.academiccontext.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.id.BusinessIdGenerator;
import com.edunest.backend.modules.academiccontext.dto.SubjectSelectionResponse;
import com.edunest.backend.modules.academiccontext.dto.SubjectSelectionUpdateRequest;
import com.edunest.backend.modules.academiccontext.entity.UserSubjectSelection;
import com.edunest.backend.modules.academiccontext.repository.AcademicSubjectResolutionRepository;
import com.edunest.backend.modules.academiccontext.repository.UserSubjectSelectionRepository;
import com.edunest.backend.modules.academiccontext.service.SubjectSelectionService;
import com.edunest.backend.modules.subject.entity.SubjectOffering;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectSelectionServiceImpl implements SubjectSelectionService {

    private static final Set<String> SELECTABLE_CATEGORIES =
            Set.of("ELECTIVE", "HONOR", "PRACTICAL");

    private final UserAcademicProfileRepository profileRepository;
    private final AcademicSubjectResolutionRepository subjectRepository;
    private final UserSubjectSelectionRepository selectionRepository;
    private final UserRepository userRepository;
    private final BusinessIdGenerator businessIdGenerator;

    @Override
    @Transactional(readOnly = true)
    public SubjectSelectionResponse getCurrentUserSelections() {
        UserAcademicProfile profile = getValidatedProfile();
        Long userId = profile.getUser().getId();

        Set<String> contextOfferingIds = resolveContextOfferings(profile).stream()
                .map(SubjectOffering::getId)
                .collect(Collectors.toSet());

        List<String> selectedIds = selectionRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .filter(UserSubjectSelection::isActive)
                .map(UserSubjectSelection::getSubjectOffering)
                .filter(offering -> contextOfferingIds.contains(offering.getId()))
                .map(SubjectOffering::getId)
                .toList();

        return SubjectSelectionResponse.builder()
                .selectedSubjectOfferingIds(selectedIds)
                .build();
    }

    @Override
    @Transactional
    public SubjectSelectionResponse updateCurrentUserSelections(SubjectSelectionUpdateRequest request) {
        UserAcademicProfile profile = getValidatedProfile();
        Long userId = profile.getUser().getId();

        List<String> requestedIds = request.getSubjectOfferingIds() == null
                ? List.of()
                : request.getSubjectOfferingIds().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(value -> !value.isBlank())
                        .distinct()
                        .toList();

        if (requestedIds.size() > 30) {
            throw new BadRequestException("You can select at most 30 optional subjects");
        }

        List<SubjectOffering> contextOfferings = resolveContextOfferings(profile);
        Map<String, SubjectOffering> offeringById = contextOfferings.stream()
                .collect(Collectors.toMap(SubjectOffering::getId, Function.identity()));

        for (String offeringId : requestedIds) {
            SubjectOffering offering = offeringById.get(offeringId);
            if (offering == null) {
                throw new BadRequestException(
                        "Subject offering " + offeringId + " does not belong to your current academic context");
            }

            String categoryCode = offering.getCategory().getCode();
            if (!SELECTABLE_CATEGORIES.contains(categoryCode)) {
                throw new BadRequestException(
                        "Regular subjects are added automatically and cannot be manually selected");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<String> requestedSet = new LinkedHashSet<>(requestedIds);

        selectionRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .forEach(selection -> selection.setActive(false));

        for (String offeringId : requestedSet) {
            UserSubjectSelection selection = selectionRepository
                    .findByUserIdAndSubjectOfferingId(userId, offeringId)
                    .orElseGet(() -> UserSubjectSelection.builder()
                            .id(businessIdGenerator.nextId("USS"))
                            .user(user)
                            .subjectOffering(offeringById.get(offeringId))
                            .active(true)
                            .build());

            selection.setActive(true);
            selectionRepository.save(selection);
        }

        return SubjectSelectionResponse.builder()
                .selectedSubjectOfferingIds(new ArrayList<>(requestedSet))
                .build();
    }

    private List<SubjectOffering> resolveContextOfferings(UserAcademicProfile profile) {
        return subjectRepository.findSubjectsForContext(
                profile.getUniversity().getId(),
                profile.getBranch().getId(),
                profile.getProgram().getId(),
                profile.getExamPattern().getId(),
                profile.getCurrentSemester().getId(),
                resolveStudyYear(profile));
    }

    private UserAcademicProfile getValidatedProfile() {
        Long userId = SecurityUtils.getCurrentUserId();

        UserAcademicProfile profile = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));

        if (profile.getUniversity() == null || profile.getBranch() == null
                || profile.getAcademicYear() == null || profile.getCurrentSemester() == null) {
            throw new BadRequestException(
                    "Complete university, branch, academic year and semester before managing subjects");
        }
        if (profile.getProgram() == null) {
            throw new BadRequestException("Academic program is required before managing subjects");
        }
        if (profile.getExamPattern() == null) {
            throw new BadRequestException("Exam pattern is required before managing subjects");
        }
        Integer studyYear = resolveStudyYear(profile);
        if (studyYear == null || studyYear < 1 || studyYear > 4) {
            throw new BadRequestException(
                    "Current academic year must be between 1 and 4 before managing subjects");
        }

        return profile;
    }

    /**
     * Resolve the curriculum study year from the user's authoritative semester.
     *
     * The profile's currentYear is onboarding/profile data and can become stale
     * when a user changes academic year or semester. Subject resolution must
     * therefore not depend on that stale value. For SPPU, semesters have a
     * fixed study-year mapping:
     *   1-2 -> FE, 3-4 -> SE, 5-6 -> TE, 7-8 -> BE.
     *
     * We still fall back to currentYear for non-SPPU institutions because their
     * semester-to-year rules may differ.
     */
    private Integer resolveStudyYear(UserAcademicProfile profile) {
        if (profile.getCurrentSemester() != null
                && profile.getUniversity() != null
                && "SPPU".equalsIgnoreCase(profile.getUniversity().getShortCode())) {
            Integer semesterNumber = profile.getCurrentSemester().getNumber();
            if (semesterNumber != null && semesterNumber >= 1 && semesterNumber <= 8) {
                return (semesterNumber + 1) / 2;
            }
        }

        String code = profile.getAcademicYear() == null || profile.getAcademicYear().getCode() == null
                ? "" : profile.getAcademicYear().getCode().toUpperCase();
        if (code.startsWith("SPPU-FE-")) return 1;
        if (code.startsWith("SPPU-SE-")) return 2;
        if (code.startsWith("SPPU-TE-")) return 3;
        if (code.startsWith("SPPU-BE-")) return 4;
        return profile.getCurrentYear();
    }
}
