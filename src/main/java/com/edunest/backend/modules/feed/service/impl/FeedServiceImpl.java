package com.edunest.backend.modules.feed.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.modules.feed.dto.response.FeedResponse;
import com.edunest.backend.modules.feed.service.FeedService;
import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import com.edunest.backend.modules.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final UserAcademicProfileRepository profileRepository;
    private final ResourceRepository resourceRepository;
    private final StorageService storageService;
    

    @Override
    public FeedResponse getFeed(Long userId) {

        UserAcademicProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found"));

        List<Resource> resources =
                resourceRepository
                        .findByBranch_IdAndSemester_IdAndActiveTrueAndPublishedTrue(
                                profile.getBranch().getId(),
                                profile.getCurrentSemester().getId()
                        );

        List<ResourceResponse> resourceResponses =
                resources.stream()
                        .map(this::mapResource)
                        .collect(Collectors.toList());

        return FeedResponse.builder()
                .userId(userId)
                .userName(profile.getUser().getFullName())
                .branchName(profile.getBranch().getName())
                .semesterName(profile.getCurrentSemester().getName())
                .resourceCount(resourceResponses.size())
                .resources(resourceResponses)
                .build();
    }

    private ResourceResponse mapResource(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .slug(resource.getSlug())
                .description(resource.getDescription())

                .categoryName(resource.getCategory() != null
                        ? resource.getCategory().getName()
                        : null)

                .branchName(resource.getBranch() != null
                        ? resource.getBranch().getName()
                        : null)

                .semesterNumber(resource.getSemester() != null
                        ? resource.getSemester().getNumber()
                        : null)

                .subjectName(resource.getSubject() != null
                        ? resource.getSubject().getName()
                        : null)

                .materialType(resource.getMaterialType())
                .accessType(resource.getAccessType())

                .price(resource.getPrice())
                .discountPrice(resource.getDiscountPrice())

                .free(resource.getAccessType() == AccessType.FREE)
                .discounted(resource.getDiscountPrice() != null)

                .previewPages(resource.getPreviewPages())
                .pageCount(resource.getPageCount())
                .fileSizeBytes(resource.getFileSizeBytes())

                .signedPreviewUrl(
                        storageService.generatePublicUrl(resource.getPreviewKey())
                )
                .thumbnailUrl(
                        storageService.generatePublicUrl(resource.getThumbnailUrl())
                )
                .coverImageUrl(
                        storageService.generatePublicUrl(resource.getCoverImageUrl())
                )

                .version(resource.getVersion())
                .language(resource.getLanguage())
                .tags(resource.getTags())

                .downloadable(resource.isDownloadable())
                .fileKey(resource.getFileKey())

                .downloadsCount(resource.getDownloadsCount())
                .ratingAverage(resource.getRatingAverage())
                .ratingCount(resource.getRatingCount())
                .popularityScore(resource.getPopularityScore())
                .build();
    }
}