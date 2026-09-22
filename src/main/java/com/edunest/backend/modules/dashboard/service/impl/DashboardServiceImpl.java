package com.edunest.backend.modules.dashboard.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.modules.dashboard.dto.DashboardResponse;
import com.edunest.backend.modules.dashboard.service.DashboardService;
import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.subject.dto.SubjectResponseDto;
import com.edunest.backend.modules.subject.entity.Subject;
import com.edunest.backend.modules.subject.repository.SubjectRepository;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserAcademicProfileRepository profileRepository;
    private final SubjectRepository subjectRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public DashboardResponse getDashboard(Long userId) {

        UserAcademicProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        List<SubjectResponseDto> subjects =
                subjectRepository
                        .findByBranchIdAndSemesterIdAndActiveTrue(
                                profile.getBranch().getId(),
                                profile.getCurrentSemester().getId())
                        .stream()
                        .map(this::mapSubject)
                        .collect(Collectors.toList());

        List<ResourceResponse> resources =
                resourceRepository
                        .findByBranch_IdAndSemester_IdAndActiveTrueAndPublishedTrue(
                                profile.getBranch().getId(),
                                profile.getCurrentSemester().getId())
                        .stream()
                        .limit(10)
                        .map(this::mapResource)
                        .collect(Collectors.toList());

        return DashboardResponse.builder()
                .userId(userId)
                .userName(profile.getUser().getFullName())
                .profileCompleted(true)
                .branchName(profile.getBranch().getName())
                .semesterName(profile.getCurrentSemester().getName())
                .subjectCount(subjects.size())
                .resourceCount(resources.size())
                .subjects(subjects)
                .recentResources(resources)
                .build();
    }

    private SubjectResponseDto mapSubject(Subject s) {
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

    private ResourceResponse mapResource(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .slug(resource.getSlug())
                .description(resource.getDescription())
                .subjectName(resource.getSubject() != null
                        ? resource.getSubject().getName()
                        : null)
                .materialType(resource.getMaterialType())
                .accessType(resource.getAccessType())
                .price(resource.getPrice())
                .discountPrice(resource.getDiscountPrice())
                .free(resource.getAccessType() == AccessType.FREE)
                .downloadable(resource.isDownloadable())
                .build();
    }
}