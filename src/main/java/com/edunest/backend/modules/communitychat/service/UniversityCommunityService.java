package com.edunest.backend.modules.communitychat.service;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import com.edunest.backend.modules.communitychat.dto.response.CommunityRoomResponse;
import com.edunest.backend.modules.communitychat.entity.CommunityChannel;
import com.edunest.backend.modules.communitychat.entity.CommunityChannelType;
import com.edunest.backend.modules.communitychat.repository.CommunityChannelRepository;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversityCommunityService {

    private final UserAcademicProfileRepository profileRepository;
    private final CommunityChannelRepository channelRepository;
    private final CollegeBranchRepository collegeBranchRepository;

    @Transactional
    public List<CommunityRoomResponse> getCurrentGroups(Long userId) {
        UserAcademicProfile profile = activeProfile(userId);
        Long universityId = profile.getUniversity().getId();
        Long collegeId = profile.getCollege().getId();
        Long branchId = profile.getBranch().getId();

        if (!universityId.equals(profile.getCollege().getUniversity().getId())
                || !universityId.equals(profile.getBranch().getUniversity().getId())) {
            throw new AccessDeniedException("Your college, branch and university selections do not match");
        }
        if (!collegeBranchRepository.existsByCollege_IdAndBranch_IdAndActiveTrue(collegeId, branchId)) {
            throw new AccessDeniedException("Your selected branch is not active for this college");
        }

        // Only these two rooms are returned to this user's UI.
        channelRepository.insertUniversityBranchChannelIfAbsent(universityId, branchId);
        channelRepository.insertCollegeBranchChannelIfAbsent(collegeId, branchId);

        CommunityChannel universityBranch = channelRepository
                .findByChannelTypeAndUniversity_IdAndBranch_Id(
                        CommunityChannelType.UNIVERSITY_BRANCH, universityId, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("University branch group could not be created"));
        CommunityChannel collegeBranch = channelRepository
                .findByChannelTypeAndCollege_IdAndBranch_Id(
                        CommunityChannelType.BRANCH, collegeId, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("College branch group could not be created"));

        return List.of(
                response(universityBranch, "UNIVERSITY_BRANCH",
                        profile.getBranch().getName() + " · University Branch Group", profile,
                        profileRepository.countByUniversity_IdAndBranch_IdAndActiveTrue(universityId, branchId)),
                response(collegeBranch, "BRANCH",
                        profile.getBranch().getName() + " · College Branch Group", profile,
                        profileRepository.countByCollege_IdAndBranch_IdAndActiveTrue(collegeId, branchId))
        );
    }

    private CommunityRoomResponse response(
            CommunityChannel channel, String type, String displayName,
            UserAcademicProfile profile, long count) {
        return CommunityRoomResponse.builder()
                .id(channel.getId())
                .groupType(type)
                .displayName(displayName)
                .universityId(profile.getUniversity().getId())
                .universityName(profile.getUniversity().getName())
                .collegeId(profile.getCollege().getId())
                .collegeName(profile.getCollege().getName())
                .branchId(profile.getBranch().getId())
                .branchName(profile.getBranch().getName())
                .memberCount(count)
                .semesterName(profile.getCurrentSemester().getName())
                .currentYear(profile.getCurrentYear())
                .active(channel.isActive())
                .build();
    }

    private UserAcademicProfile activeProfile(Long userId) {
        return profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new AccessDeniedException(
                        "Complete your academic profile to access the community"));
    }
}
