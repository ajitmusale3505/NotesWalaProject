package com.edunest.backend.modules.resource.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.academiccontext.repository.AcademicSubjectResolutionRepository;
import com.edunest.backend.modules.resource.dto.response.AcademicResourceResponse;
import com.edunest.backend.modules.resource.repository.AcademicResourceRepository;
import com.edunest.backend.modules.resource.service.AcademicResourceDiscoveryService;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicResourceDiscoveryServiceImpl implements AcademicResourceDiscoveryService {

    private final UserAcademicProfileRepository profileRepository;
    private final AcademicSubjectResolutionRepository contextRepository;
    private final AcademicResourceRepository resourceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AcademicResourceResponse> getCurrentUserResources(String subjectOfferingId) {
        UserAcademicProfile profile = profileRepository.findByUserIdAndActiveTrue(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic profile not found"));

        if (profile.getProgram() == null || profile.getExamPattern() == null
                || profile.getCurrentYear() == null) {
            throw new BadRequestException("Complete academic profile before resolving resources");
        }

        var curriculumSemester = contextRepository.findContext(
                profile.getUniversity().getId(),
                profile.getBranch().getId(),
                profile.getProgram().getId(),
                profile.getExamPattern().getId(),
                profile.getCurrentSemester().getId(),
                profile.getCurrentYear());

        if (curriculumSemester == null) {
            throw new ResourceNotFoundException("Academic curriculum context not found");
        }

        return resourceRepository.findForCurrentAcademicContext(
                        profile.getUniversity().getId(),
                        profile.getBranch().getId(),
                        profile.getProgram().getId(),
                        profile.getExamPattern().getId(),
                        profile.getCurrentSemester().getId(),
                        profile.getCurrentYear(),
                        subjectOfferingId)
                .stream()
                .map(this::map)
                .toList();
    }

    private AcademicResourceResponse map(com.edunest.backend.modules.resource.entity.Resource r) {
        var so = r.getSubjectOffering();
        var s = so.getSubject();
        var c = so.getCategory();

        return AcademicResourceResponse.builder()
                .resourceId(r.getId())
                .title(r.getTitle())
                .slug(r.getSlug())
                .description(r.getDescription())
                .subjectOfferingId(so.getId())
                .subjectId(s.getBusinessId())
                .subjectCode(so.getCode())
                .subjectName(s.getName())
                .categoryId(c.getId())
                .categoryCode(c.getCode())
                .categoryName(c.getName())
                .credits(so.getCredits())
                .materialType(r.getMaterialType())
                .documentType(r.getDocumentType())
                .accessType(r.getAccessType())
                .price(r.getPrice())
                .discountPrice(r.getDiscountPrice())
                .downloadable(r.isDownloadable())
                .watermarkEnabled(r.isWatermarkEnabled())
                .thumbnailUrl(r.getThumbnailUrl())
                .coverImageUrl(r.getCoverImageUrl())
                .language(r.getLanguage())
                .ratingAverage(r.getRatingAverage())
                .ratingCount(r.getRatingCount())
                .build();
    }
}
