package com.edunest.backend.modules.communitychat.service;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMemberResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityRoomResponse;
import com.edunest.backend.modules.communitychat.entity.CommunityChannel;
import com.edunest.backend.modules.communitychat.entity.CommunityChannelType;
import com.edunest.backend.modules.communitychat.repository.CommunityChannelRepository;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UniversityCommunityService {

    private static final int MAX_MEMBER_PAGE_SIZE = 50;

    private final UserAcademicProfileRepository profileRepository;
    private final CommunityChannelRepository channelRepository;

    @Transactional
    public CommunityRoomResponse getCurrentRoom(Long userId) {
        UserAcademicProfile profile = activeProfile(userId);
        Long universityId = profile.getUniversity().getId();

        // Use a database uniqueness constraint and a single INSERT to handle concurrent first visits.
        channelRepository.insertUniversityChannelIfAbsent(universityId);
        CommunityChannel channel = channelRepository
                .findByChannelTypeAndUniversity_Id(CommunityChannelType.UNIVERSITY, universityId)
                .orElseThrow(() -> new ResourceNotFoundException("University community could not be created"));

        return CommunityRoomResponse.builder()
                .id(channel.getId())
                .universityId(universityId)
                .universityName(profile.getUniversity().getName())
                .memberCount(profileRepository.countByUniversity_IdAndActiveTrue(universityId))
                .branchName(profile.getBranch().getName())
                .semesterName(profile.getCurrentSemester().getName())
                .currentYear(profile.getCurrentYear())
                .active(channel.isActive())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<CommunityMemberResponse> getCurrentMembers(Long userId, int page, int limit) {
        UserAcademicProfile profile = activeProfile(userId);
        int boundedPage = Math.max(0, page);
        int boundedLimit = Math.min(Math.max(1, limit), MAX_MEMBER_PAGE_SIZE);
        return profileRepository.findByUniversity_IdAndActiveTrue(
                        profile.getUniversity().getId(), PageRequest.of(boundedPage, boundedLimit))
                .map(member -> CommunityMemberResponse.builder()
                        .userId(member.getUser().getId())
                        .fullName(member.getUser().getFullName())
                        .branchName(member.getBranch().getName())
                        .semesterName(member.getCurrentSemester().getName())
                        .currentYear(member.getCurrentYear())
                        .build());
    }

    private UserAcademicProfile activeProfile(Long userId) {
        return profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new AccessDeniedException(
                        "Complete your academic profile to access the university community"));
    }
}
