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
        Long semesterId = profile.getCurrentSemester().getId();

        if (!universityId.equals(profile.getCollege().getUniversity().getId())
                || !universityId.equals(profile.getBranch().getUniversity().getId())) {
            throw new AccessDeniedException("Your college, branch and university selections do not match");
        }
        if (!collegeBranchRepository.existsByCollege_IdAndBranch_IdAndActiveTrue(collegeId, branchId)) {
            throw new AccessDeniedException("Your selected branch is not active for this college");
        }

        CommunityChannel universityBranch = channelRepository
                .findByChannelTypeAndUniversity_IdAndBranch_IdAndSemester_Id(
                        CommunityChannelType.UNIVERSITY_BRANCH, universityId, branchId, semesterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Your university branch group has not been created by an administrator yet."));
        if (!universityBranch.isActive()) {
            throw new AccessDeniedException("Your university branch group is currently inactive");
        }

        String collegeGroupName = profile.getBranch().getName() + " · College Branch Group";
        channelRepository.insertCollegeBranchChannelIfAbsent(collegeId, branchId, semesterId, collegeGroupName);
        CommunityChannel collegeBranch = channelRepository
                .findByChannelTypeAndCollege_IdAndBranch_IdAndSemester_Id(
                        CommunityChannelType.BRANCH, collegeId, branchId, semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("College branch group could not be created"));
        if (!collegeBranch.isActive()) {
            throw new AccessDeniedException("Your college branch group is currently inactive");
        }

        return List.of(
                response(universityBranch, "UNIVERSITY_BRANCH",
                        profile, profileRepository.countByUniversity_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
                                universityId, branchId, semesterId)),
                response(collegeBranch, "BRANCH",
                        profile, profileRepository.countByCollege_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
                                collegeId, branchId, semesterId))
        );
    }

    private CommunityRoomResponse response(
            CommunityChannel channel, String type, UserAcademicProfile profile, long count) {
        return CommunityRoomResponse.builder()
                .id(channel.getId())
                .groupType(type)
                .displayName(channel.getName() == null || channel.getName().isBlank()
                        ? profile.getBranch().getName() + (type.equals("UNIVERSITY_BRANCH")
                            ? " · University Branch Group" : " · College Branch Group")
                        : channel.getName())
                .description(channel.getDescription())
                .iconUrl(channel.getIconUrl())
                .universityId(profile.getUniversity().getId())
                .universityName(profile.getUniversity().getName())
                .collegeId(profile.getCollege().getId())
                .collegeName(profile.getCollege().getName())
                .branchId(profile.getBranch().getId())
                .branchName(profile.getBranch().getName())
                .memberCount(count)
                .semesterName(channel.getSemester() == null
                        ? profile.getCurrentSemester().getName() : channel.getSemester().getName())
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
