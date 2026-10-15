package com.edunest.backend.modules.communitychat.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.communitychat.dto.response.CommunityRoomResponse;
import com.edunest.backend.modules.communitychat.entity.CommunityChannel;
import com.edunest.backend.modules.communitychat.entity.CommunityChannelType;
import com.edunest.backend.modules.communitychat.repository.CommunityChannelRepository;
import com.edunest.backend.modules.communitychat.service.UniversityCommunityService;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/channels")
@RequiredArgsConstructor
public class CommunityChannelController {

    private final CommunityChannelRepository repository;
    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final UniversityCommunityService universityCommunityService;

    /**
     * Returns exactly the two groups allowed by the current user's active academic profile:
     * their university + branch group and their own college + branch group.
     */
    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<List<CommunityRoomResponse>>> currentUserGroups() {
        return ResponseEntity.ok(ApiResponse.<List<CommunityRoomResponse>>builder()
                .success(true)
                .message("Your two community groups were loaded successfully")
                .data(universityCommunityService.getCurrentGroups(SecurityUtils.getCurrentUserId()))
                .build());
    }

    // Administrative provisioning endpoints retained for compatibility with existing admin tooling.
    @PostMapping("/college/{collegeId}")
    public ResponseEntity<ApiResponse<Long>> createCollege(@PathVariable Long collegeId) {
        if (!SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("Only administrators can create community channels");
        }
        College college = collegeRepository.findById(collegeId).orElseThrow();
        CommunityChannel channel = repository
                .findByChannelTypeAndCollege_IdAndBranch_Id(CommunityChannelType.COLLEGE, collegeId, null)
                .orElseGet(() -> repository.save(CommunityChannel.builder()
                        .channelType(CommunityChannelType.COLLEGE)
                        .college(college)
                        .active(true)
                        .build()));
        return ResponseEntity.ok(ApiResponse.<Long>builder().success(true)
                .message("College channel ready").data(channel.getId()).build());
    }

    @PostMapping("/college/{collegeId}/branch/{branchId}")
    public ResponseEntity<ApiResponse<Long>> createBranch(
            @PathVariable Long collegeId, @PathVariable Long branchId) {
        if (!SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("Only administrators can create community channels");
        }
        College college = collegeRepository.findById(collegeId).orElseThrow();
        Branch branch = branchRepository.findById(branchId).orElseThrow();
        if (!college.getUniversity().getId().equals(branch.getUniversity().getId())) {
            throw new IllegalArgumentException("The branch does not belong to this college's university");
        }
        CommunityChannel channel = repository
                .findByChannelTypeAndCollege_IdAndBranch_Id(CommunityChannelType.BRANCH, collegeId, branchId)
                .orElseGet(() -> repository.save(CommunityChannel.builder()
                        .channelType(CommunityChannelType.BRANCH)
                        .college(college)
                        .branch(branch)
                        .active(true)
                        .build()));
        return ResponseEntity.ok(ApiResponse.<Long>builder().success(true)
                .message("College branch channel ready").data(channel.getId()).build());
    }
}
