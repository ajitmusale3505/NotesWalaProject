package com.edunest.backend.modules.communitychat.service;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.communitychat.dto.request.AdminCommunityGroupRequest;
import com.edunest.backend.modules.communitychat.dto.response.AdminCommunityGroupResponse;
import com.edunest.backend.modules.communitychat.dto.response.AdminCommunityGroupMemberResponse;
import com.edunest.backend.modules.communitychat.dto.response.AdminCommunityGroupStatisticsResponse;
import com.edunest.backend.modules.communitychat.dto.response.AdminCommunityGroupSummaryResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.entity.CommunityChannel;
import com.edunest.backend.modules.communitychat.entity.CommunityChannelType;
import com.edunest.backend.modules.communitychat.entity.CommunityMessage;
import com.edunest.backend.modules.communitychat.entity.CommunityMessageStatus;
import com.edunest.backend.modules.communitychat.repository.CommunityChannelRepository;
import com.edunest.backend.modules.communitychat.repository.CommunityMessageRepository;
import com.edunest.backend.modules.communitychat.repository.CommunityReportRepository;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.semester.repository.SemesterRepository;
import com.edunest.backend.modules.storage.dto.UploadResponse;
import com.edunest.backend.modules.storage.service.StorageService;
import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.university.repository.UniversityRepository;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommunityGroupService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final long MAX_ICON_BYTES = 2L * 1024 * 1024;

    private final CommunityChannelRepository channelRepository;
    private final CommunityMessageRepository messageRepository;
    private final CommunityReportRepository reportRepository;
    private final UniversityRepository universityRepository;
    private final BranchRepository branchRepository;
    private final SemesterRepository semesterRepository;
    private final UserAcademicProfileRepository profileRepository;
    private final StorageService storageService;

    public Page<AdminCommunityGroupResponse> list(
            int page, int size, String query, String universityPublicId, String status) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        Long universityId = universityPublicId == null || universityPublicId.isBlank()
                ? null : PublicIdUtils.parseUniversityId(universityPublicId);
        Boolean active = switch (status == null ? "all" : status.trim().toLowerCase(Locale.ROOT)) {
            case "active" -> true;
            case "inactive" -> false;
            default -> null;
        };
        // Always bind a non-null String so PostgreSQL does not infer bytea for lower(:query).
        String normalizedQuery = query == null || query.isBlank() ? "" : query.trim();

        Page<CommunityChannel> groups = channelRepository.searchAdminGroups(
                CommunityChannelType.UNIVERSITY_BRANCH,
                universityId,
                active,
                normalizedQuery,
                PageRequest.of(safePage, safeSize));

        if (groups.isEmpty()) {
            return groups.map(group -> toResponse(group, 0L));
        }
        List<Long> ids = groups.getContent().stream().map(CommunityChannel::getId).toList();
        Map<Long, Long> memberCounts = channelRepository.countMembersForGroupIds(ids).stream()
                .collect(Collectors.toMap(
                        CommunityChannelRepository.CommunityGroupMemberCount::getChannelId,
                        item -> item.getMemberCount() == null ? 0L : item.getMemberCount()));
        return groups.map(group -> toResponse(group, memberCounts.getOrDefault(group.getId(), 0L)));
    }

    public AdminCommunityGroupResponse getGroup(Long id) {
        CommunityChannel channel = findGroup(id);
        return toResponse(channel, countMembers(channel));
    }

    public AdminCommunityGroupStatisticsResponse statistics(Long id) {
        CommunityChannel channel = findGroup(id);
        Long universityId = channel.getUniversity().getId();
        Long branchId = channel.getBranch().getId();
        Long semesterId = channel.getSemester().getId();

        long totalMembers = profileRepository.countByUniversity_IdAndBranch_IdAndCurrentSemester_Id(
                universityId, branchId, semesterId);
        long activeMembers = profileRepository.countByUniversity_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
                universityId, branchId, semesterId);
        long inactiveMembers = Math.max(0, totalMembers - activeMembers);
        long totalMessages = messageRepository.countByChannel_IdAndStatus(
                channel.getId(), CommunityMessageStatus.ACTIVE);
        long suspiciousUsers = reportRepository.countDistinctReportedAuthorsByChannelId(channel.getId());

        return AdminCommunityGroupStatisticsResponse.builder()
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .inactiveMembers(inactiveMembers)
                .totalMessages(totalMessages)
                .suspiciousUsers(suspiciousUsers)
                .activePercentage(totalMembers == 0 ? 0 : Math.round(activeMembers * 1000.0 / totalMembers) / 10.0)
                .inactivePercentage(totalMembers == 0 ? 0 : Math.round(inactiveMembers * 1000.0 / totalMembers) / 10.0)
                .build();
    }

    public Page<AdminCommunityGroupMemberResponse> members(
            Long id, int page, int size, String query, String status, String activityLevel) {
        CommunityChannel channel = findGroup(id);
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        String normalizedQuery = query == null || query.isBlank() ? "" : query.trim();
        String normalizedStatus = switch (status == null ? "all" : status.trim().toLowerCase(Locale.ROOT)) {
            case "active" -> "active";
            case "inactive" -> "inactive";
            default -> "all";
        };
        String normalizedActivity = switch (activityLevel == null ? "all" : activityLevel.trim().toLowerCase(Locale.ROOT)) {
            case "none", "low", "medium", "high" -> activityLevel.trim().toLowerCase(Locale.ROOT);
            default -> "all";
        };

        Page<com.edunest.backend.modules.userprofile.entity.UserAcademicProfile> profiles = profileRepository
                .searchAdminGroupMembers(
                        channel.getId(),
                        channel.getUniversity().getId(),
                        channel.getBranch().getId(),
                        channel.getSemester().getId(),
                        normalizedQuery,
                        normalizedStatus,
                        normalizedActivity,
                        PageRequest.of(safePage, safeSize));

        if (profiles.isEmpty()) {
            return profiles.map(profile -> toMemberResponse(profile, 0L, 0L));
        }

        List<Long> userIds = profiles.getContent().stream()
                .map(profile -> profile.getUser().getId())
                .toList();
        Map<Long, Long> messageCounts = messageRepository.countActiveMessagesByAuthors(channel.getId(), userIds).stream()
                .collect(Collectors.toMap(
                        CommunityMessageRepository.AuthorMessageCount::getUserId,
                        item -> item.getCountValue() == null ? 0L : item.getCountValue()));
        Map<Long, Long> reportCounts = reportRepository.countReportsByAuthors(channel.getId(), userIds).stream()
                .collect(Collectors.toMap(
                        CommunityReportRepository.AuthorReportCount::getUserId,
                        item -> item.getCountValue() == null ? 0L : item.getCountValue()));

        return profiles.map(profile -> toMemberResponse(
                profile,
                messageCounts.getOrDefault(profile.getUser().getId(), 0L),
                reportCounts.getOrDefault(profile.getUser().getId(), 0L)));
    }

    public Page<CommunityMessageResponse> messages(Long id, int page, int size) {
        CommunityChannel channel = findGroup(id);
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        return messageRepository.findByChannel_IdAndStatusOrderByCreatedAtDesc(
                        channel.getId(), CommunityMessageStatus.ACTIVE, PageRequest.of(safePage, safeSize))
                .map(this::toMessageResponse);
    }

    private AdminCommunityGroupMemberResponse toMemberResponse(
            com.edunest.backend.modules.userprofile.entity.UserAcademicProfile profile,
            long messageCount,
            long suspiciousActivityCount) {
        String phone = profile.getPhoneNumber();
        if (phone == null || phone.isBlank()) {
            phone = profile.getUser().getPhoneNumber();
        }
        return AdminCommunityGroupMemberResponse.builder()
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .rollNumber(profile.getRollNumber())
                .email(profile.getUser().getEmail())
                .phoneNumber(phone)
                .semesterName(profile.getCurrentSemester() == null ? null : profile.getCurrentSemester().getName())
                .branchName(profile.getBranch() == null ? null : profile.getBranch().getName())
                .currentYear(profile.getCurrentYear())
                .messageCount(messageCount)
                .suspiciousActivityCount(suspiciousActivityCount)
                .active(profile.isActive())
                .joinedAt(profile.getCreatedAt())
                .build();
    }

    private CommunityMessageResponse toMessageResponse(CommunityMessage message) {
        return CommunityMessageResponse.builder()
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .authorId(message.getAuthor().getId())
                .parentMessageId(message.getParentMessage() == null ? null : message.getParentMessage().getId())
                .clientMessageId(message.getClientMessageId())
                .authorName(message.getAuthor().getFullName())
                .content(message.getContent())
                .status(message.getStatus().name())
                .pinned(message.isPinned())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    public AdminCommunityGroupSummaryResponse summary() {
        long total = channelRepository.countByChannelTypeAndSemesterIsNotNull(CommunityChannelType.UNIVERSITY_BRANCH);
        long active = channelRepository.countByChannelTypeAndSemesterIsNotNullAndActiveTrue(CommunityChannelType.UNIVERSITY_BRANCH);
        long inactive = Math.max(0, total - active);
        double activePercentage = total == 0 ? 0 : Math.round(active * 1000.0 / total) / 10.0;
        double inactivePercentage = total == 0 ? 0 : Math.round(inactive * 1000.0 / total) / 10.0;
        return AdminCommunityGroupSummaryResponse.builder()
                .totalGroups(total)
                .activeGroups(active)
                .inactiveGroups(inactive)
                .totalMembers(profileRepository.countActiveMembersMappedToUniversityBranchGroups())
                .activePercentage(activePercentage)
                .inactivePercentage(inactivePercentage)
                .build();
    }

    @Transactional
    public AdminCommunityGroupResponse create(AdminCommunityGroupRequest request) {
        ValidatedScope scope = validateScope(request);
        Optional<CommunityChannel> duplicate = channelRepository
                .findByChannelTypeAndUniversity_IdAndBranch_IdAndSemester_Id(
                        CommunityChannelType.UNIVERSITY_BRANCH,
                        scope.university().getId(),
                        scope.branch().getId(),
                        scope.semester().getId());
        if (duplicate.isPresent()) {
            throw new BadRequestException("A group already exists for this university, branch and semester. Edit that group instead.");
        }

        String name = request.getName().trim();
        CommunityChannel channel = CommunityChannel.builder()
                .channelType(CommunityChannelType.UNIVERSITY_BRANCH)
                .university(scope.university())
                .branch(scope.branch())
                .semester(scope.semester())
                .name(name)
                .description(trimToNull(request.getDescription()))
                .active(request.getActive() == null || request.getActive())
                .build();
        CommunityChannel saved = channelRepository.save(channel);
        return toResponse(saved, profileRepository.countByUniversity_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
                scope.university().getId(), scope.branch().getId(), scope.semester().getId()));
    }

    @Transactional
    public AdminCommunityGroupResponse update(Long id, AdminCommunityGroupRequest request) {
        CommunityChannel channel = findGroup(id);
        ValidatedScope scope = validateScope(request);

        Optional<CommunityChannel> duplicate = channelRepository
                .findByChannelTypeAndUniversity_IdAndBranch_IdAndSemester_Id(
                        CommunityChannelType.UNIVERSITY_BRANCH,
                        scope.university().getId(),
                        scope.branch().getId(),
                        scope.semester().getId());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(channel.getId())) {
            throw new BadRequestException("Another group already exists for this university, branch and semester.");
        }

        channel.setUniversity(scope.university());
        channel.setBranch(scope.branch());
        channel.setSemester(scope.semester());
        channel.setName(request.getName().trim());
        channel.setDescription(trimToNull(request.getDescription()));
        if (request.getActive() != null) channel.setActive(request.getActive());
        CommunityChannel saved = channelRepository.save(channel);
        return toResponse(saved, profileRepository.countByUniversity_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
                scope.university().getId(), scope.branch().getId(), scope.semester().getId()));
    }

    @Transactional
    public AdminCommunityGroupResponse changeStatus(Long id, boolean active) {
        CommunityChannel channel = findGroup(id);
        channel.setActive(active);
        CommunityChannel saved = channelRepository.save(channel);
        return toResponse(saved, countMembers(saved));
    }

    @Transactional
    public AdminCommunityGroupResponse uploadIcon(Long id, MultipartFile file) {
        CommunityChannel channel = findGroup(id);
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Choose a group icon image to upload.");
        }
        if (file.getSize() > MAX_ICON_BYTES) {
            throw new BadRequestException("Group icon must be 2 MB or smaller.");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!Set.of("image/png", "image/jpeg", "image/webp").contains(contentType)) {
            throw new BadRequestException("Group icon must be a PNG, JPG or WebP image.");
        }

        UploadResponse upload = storageService.uploadImage(file, "community-groups");
        // Store the stable R2 object key; r2://bucket/key is not a browser-loadable image URL.
        channel.setIconUrl(upload.getFileName());
        return toResponse(channelRepository.save(channel), countMembers(channel));
    }

    private CommunityChannel findGroup(Long id) {
        return channelRepository.findByIdAndChannelType(id, CommunityChannelType.UNIVERSITY_BRANCH)
                .orElseThrow(() -> new ResourceNotFoundException("Community group not found."));
    }

    private ValidatedScope validateScope(AdminCommunityGroupRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Group name is required.");
        }
        String name = request.getName().trim();
        if (name.length() > 120) throw new BadRequestException("Group name must be 120 characters or fewer.");
        if (request.getDescription() != null && request.getDescription().length() > 500) {
            throw new BadRequestException("Description must be 500 characters or fewer.");
        }

        Long universityId = PublicIdUtils.parseUniversityId(request.getUniversityId());
        Long branchId = PublicIdUtils.parseBranchId(request.getBranchId());
        Long semesterId = PublicIdUtils.parseSemesterId(request.getSemesterId());

        University university = universityRepository.findByIdAndActiveTrue(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Selected university is not active."));
        Branch branch = branchRepository.findByIdAndActiveTrue(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Selected branch is not active."));
        Semester semester = semesterRepository.findByIdAndActiveTrue(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Selected semester is not active."));

        if (branch.getUniversity() == null || !university.getId().equals(branch.getUniversity().getId())) {
            throw new BadRequestException("The selected branch does not belong to the selected university.");
        }
        return new ValidatedScope(university, branch, semester);
    }

    private AdminCommunityGroupResponse toResponse(CommunityChannel channel, long memberCount) {
        University university = channel.getUniversity();
        Branch branch = channel.getBranch();
        Semester semester = channel.getSemester();
        return AdminCommunityGroupResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .iconUrl(resolveIconUrl(channel.getIconUrl()))
                .universityId(university == null ? null : PublicIdUtils.universityId(university.getId()))
                .universityName(university == null ? "—" : university.getName())
                .universityShortCode(university == null ? null : university.getShortCode())
                .branchId(branch == null ? null : PublicIdUtils.branchId(branch.getId()))
                .branchName(branch == null ? "—" : branch.getName())
                .branchCode(branch == null ? null : branch.getCode())
                .semesterId(semester == null ? null : PublicIdUtils.semesterId(semester.getId()))
                .semesterName(semester == null ? "—" : semester.getName())
                .memberCount(memberCount)
                .active(channel.isActive())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .build();
    }

    private String resolveIconUrl(String storedIconUrl) {
        if (storedIconUrl == null || storedIconUrl.isBlank()) {
            return null;
        }

        String value = storedIconUrl.trim();
        if (value.startsWith("r2://")) {
            // Backward compatibility for icons persisted before the object-key fix.
            int keyStart = value.indexOf('/', "r2://".length());
            if (keyStart < 0 || keyStart == value.length() - 1) {
                return null;
            }
            value = value.substring(keyStart + 1);
        }

        // Preserve a previously configured HTTP URL if one is already stored.
        if (value.startsWith("https://") || value.startsWith("http://")) {
            return value;
        }

        // R2 signed URLs expire, so issue a fresh browser-accessible URL per API response.
        return storageService.generateImageUrl(value);
    }

    private long countMembers(CommunityChannel channel) {
        if (channel.getUniversity() == null || channel.getBranch() == null || channel.getSemester() == null) return 0;
        return profileRepository.countByUniversity_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
                channel.getUniversity().getId(), channel.getBranch().getId(), channel.getSemester().getId());
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

    private record ValidatedScope(University university, Branch branch, Semester semester) {}
}
