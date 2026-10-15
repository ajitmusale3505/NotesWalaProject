package com.edunest.backend.modules.communitychat.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import com.edunest.backend.modules.communitychat.dto.request.ReportMessageRequest;
import com.edunest.backend.modules.communitychat.dto.request.SendMessageRequest;
import com.edunest.backend.modules.communitychat.dto.response.CommunityAttachmentResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMemberResponse;
import com.edunest.backend.modules.communitychat.entity.*;
import com.edunest.backend.modules.communitychat.event.CommunityMessageCreatedEvent;
import com.edunest.backend.modules.communitychat.repository.*;
import com.edunest.backend.modules.communitychat.service.CommunityChatService;
import com.edunest.backend.modules.storage.dto.UploadResponse;
import com.edunest.backend.modules.storage.service.StorageService;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import com.edunest.backend.modules.userprofile.repository.UserAcademicProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityChatServiceImpl implements CommunityChatService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final int MAX_MESSAGES_PER_MINUTE = 20;

    private final CommunityChannelRepository channelRepository;
    private final CommunityMessageRepository messageRepository;
    private final CommunityReportRepository reportRepository;
    private final CommunityMuteRepository muteRepository;
    private final UserRepository userRepository;
    private final UserAcademicProfileRepository profileRepository;
    private final CollegeBranchRepository collegeBranchRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final StorageService storageService;
    private final CommunityAttachmentRepository attachmentRepository;

    @Override
    public Page<CommunityMessageResponse> history(Long userId, Long channelId, int page, int size) {
        authorize(userId, channelId);
        return messageRepository
                .findByChannel_IdAndStatusOrderByCreatedAtDesc(
                        channelId, CommunityMessageStatus.ACTIVE,
                        PageRequest.of(Math.max(0, page), boundedSize(size)))
                .map(this::response);
    }

    @Override
    public Page<CommunityMessageResponse> historyBefore(Long userId, Long channelId, Long beforeId, int size) {
        authorize(userId, channelId);
        if (beforeId == null || beforeId < 1) {
            throw new BadRequestException("A valid beforeId cursor is required");
        }
        return messageRepository
                .findByChannel_IdAndIdLessThanAndStatusOrderByIdDesc(
                        channelId, beforeId, CommunityMessageStatus.ACTIVE,
                        PageRequest.of(0, boundedSize(size)))
                .map(this::response);
    }

    @Override
    public Page<CommunityMessageResponse> thread(Long userId, Long channelId, Long messageId, int page, int size) {
        authorize(userId, channelId);
        CommunityMessage parent = getMessage(messageId, channelId);
        return messageRepository.findThread(channelId, parent.getId(),
                        PageRequest.of(Math.max(0, page), boundedSize(size)))
                .map(this::response);
    }

    @Override
    public Page<CommunityMemberResponse> members(Long userId, Long channelId, int page, int size) {
        CommunityChannel channel = authorize(userId, channelId);
        UserAcademicProfile actorProfile = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new AccessDeniedException("Active academic profile required"));
        Pageable pageable = PageRequest.of(Math.max(0, page), boundedSize(size));

        Page<UserAcademicProfile> profiles;
        if (channel.getChannelType() == CommunityChannelType.UNIVERSITY_BRANCH) {
            profiles = profileRepository.findByUniversity_IdAndBranch_IdAndActiveTrue(
                    actorProfile.getUniversity().getId(), actorProfile.getBranch().getId(), pageable);
        } else if (channel.getChannelType() == CommunityChannelType.BRANCH) {
            profiles = profileRepository.findByCollege_IdAndBranch_IdAndActiveTrue(
                    actorProfile.getCollege().getId(), actorProfile.getBranch().getId(), pageable);
        } else {
            throw new AccessDeniedException("Members are only available for your two assigned groups");
        }

        return profiles.map(profile -> CommunityMemberResponse.builder()
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .branchName(profile.getBranch().getName())
                .semesterName(profile.getCurrentSemester().getName())
                .currentYear(profile.getCurrentYear())
                .build());
    }

    @Override
    public CommunityMessageResponse pinned(Long userId, Long channelId) {
        authorize(userId, channelId);
        return messageRepository
                .findFirstByChannel_IdAndPinnedTrueAndStatusOrderByCreatedAtDesc(
                        channelId, CommunityMessageStatus.ACTIVE)
                .map(this::response)
                .orElse(null);
    }

    @Override
    public List<CommunityAttachmentResponse> attachments(Long userId, Long channelId, int limit) {
        authorize(userId, channelId);
        return attachmentRepository.findRecentByChannelId(
                        channelId, CommunityMessageStatus.ACTIVE, PageRequest.of(0, boundedSize(limit)))
                .stream()
                .map(this::attachmentResponse)
                .toList();
    }

    @Override
    @Transactional
    public CommunityMessageResponse send(Long userId, Long channelId, SendMessageRequest request) {
        User sender = user(userId);
        CommunityChannel channel = authorize(userId, channelId);

        if (muteRepository.existsByChannel_IdAndUser_IdAndExpiresAtAfter(
                channelId, userId, LocalDateTime.now())) {
            throw new AccessDeniedException("You are muted in this channel");
        }

        if (messageRepository.countByAuthor_IdAndCreatedAtAfter(
                userId, LocalDateTime.now().minusMinutes(1)) >= MAX_MESSAGES_PER_MINUTE) {
            throw new BadRequestException("Message rate limit exceeded");
        }

        String content = request.getContent().trim();
        if (hasExternalLink(content)) {
            throw new BadRequestException("External links are not allowed in community chat");
        }

        String clientMessageId = request.getClientMessageId();
        if (clientMessageId != null && !clientMessageId.isBlank()) {
            var previous = messageRepository.findByChannel_IdAndAuthor_IdAndClientMessageId(
                    channelId, userId, clientMessageId.trim());
            if (previous.isPresent()) {
                return response(previous.get());
            }
        }

        CommunityMessage parent = null;
        if (request.getParentMessageId() != null) {
            parent = getMessage(request.getParentMessageId(), channelId);
            if (parent.getStatus() != CommunityMessageStatus.ACTIVE) {
                throw new BadRequestException("Parent message is unavailable");
            }
        }

        CommunityMessage message = CommunityMessage.builder()
                .channel(channel)
                .author(sender)
                .parentMessage(parent)
                .clientMessageId(clientMessageId == null || clientMessageId.isBlank()
                        ? null : clientMessageId.trim())
                .content(content)
                .status(CommunityMessageStatus.ACTIVE)
                .pinned(false)
                .build();

        CommunityMessage saved = messageRepository.save(message);
        CommunityMessageResponse result = response(saved);
        eventPublisher.publishEvent(new CommunityMessageCreatedEvent(result));
        return result;
    }

    @Override
    @Transactional
    public CommunityAttachmentResponse attach(Long userId, Long channelId, Long messageId, MultipartFile file) {
        authorize(userId, channelId);
        CommunityMessage message = getMessage(messageId, channelId);
        if (!message.getAuthor().getId().equals(userId) && !isAdmin(user(userId))) {
            throw new AccessDeniedException("You cannot modify this message");
        }
        if (message.getStatus() != CommunityMessageStatus.ACTIVE) {
            throw new BadRequestException("Message is unavailable");
        }
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Attachment is required");
        }

        UploadResponse uploaded = storageService.uploadFile(file, "community-chat");
        CommunityMessageAttachment attachment = CommunityMessageAttachment.builder()
                .message(message)
                .storageKey(uploaded.getFileUrl())
                .fileName(uploaded.getOriginalFileName())
                .contentType(uploaded.getContentType())
                .sizeBytes(uploaded.getFileSizeBytes())
                .build();
        return attachmentResponse(attachmentRepository.save(attachment));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long channelId, Long messageId) {
        authorize(userId, channelId);
        CommunityMessage message = getMessage(messageId, channelId);
        User actor = user(userId);
        if (!message.getAuthor().getId().equals(userId) && !isAdmin(actor)) {
            throw new AccessDeniedException("You cannot delete this message");
        }
        message.setStatus(CommunityMessageStatus.DELETED);
        message.setContent("");
        message.setDeletedAt(LocalDateTime.now());
        message.setDeletedBy(actor);
    }

    @Override
    @Transactional
    public void pin(Long userId, Long channelId, Long messageId, boolean pinned) {
        authorize(userId, channelId);
        if (!isAdmin(user(userId))) {
            throw new AccessDeniedException("Only moderators can pin messages");
        }
        getMessage(messageId, channelId).setPinned(pinned);
    }

    @Override
    @Transactional
    public void report(Long userId, Long channelId, Long messageId, ReportMessageRequest request) {
        authorize(userId, channelId);
        CommunityMessage message = getMessage(messageId, channelId);
        if (reportRepository.existsByMessage_IdAndReporter_Id(messageId, userId)) {
            throw new BadRequestException("You already reported this message");
        }
        reportRepository.save(CommunityMessageReport.builder()
                .message(message)
                .reporter(user(userId))
                .reason(request.getReason().trim())
                .createdAt(LocalDateTime.now())
                .resolved(false)
                .build());
    }

    @Override
    @Transactional
    public void mute(Long adminUserId, Long channelId, Long userId, long minutes) {
        if (!isAdmin(user(adminUserId))) {
            throw new AccessDeniedException("Only moderators can mute users");
        }
        if (minutes < 1 || minutes > 10080) {
            throw new BadRequestException("Mute duration must be between 1 minute and 7 days");
        }
        CommunityChannel channel = channel(channelId);
        User target = user(userId);
        CommunityUserMute mute = muteRepository.findByChannel_IdAndUser_Id(channelId, userId)
                .orElse(CommunityUserMute.builder()
                        .channel(channel).user(target).mutedBy(user(adminUserId)).build());
        mute.setMutedBy(user(adminUserId));
        mute.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));
        muteRepository.save(mute);
    }

    @Override
    @Transactional
    public void unmute(Long adminUserId, Long channelId, Long userId) {
        if (!isAdmin(user(adminUserId))) {
            throw new AccessDeniedException("Only moderators can unmute users");
        }
        muteRepository.findByChannel_IdAndUser_Id(channelId, userId).ifPresent(muteRepository::delete);
    }

    @Override
    public Page<CommunityMessageReport> pendingReports(Long adminUserId, int page, int size) {
        if (!isAdmin(user(adminUserId))) {
            throw new AccessDeniedException("Only moderators can view reports");
        }
        return reportRepository.findByResolvedFalseOrderByCreatedAtAsc(
                PageRequest.of(Math.max(0, page), boundedSize(size)));
    }

    @Override
    @Transactional
    public void resolveReport(Long adminUserId, Long reportId) {
        if (!isAdmin(user(adminUserId))) {
            throw new AccessDeniedException("Only moderators can resolve reports");
        }
        CommunityMessageReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setResolved(true);
        reportRepository.save(report);
    }

    @Override
    public void assertCanAccess(Long userId, Long channelId) {
        authorize(userId, channelId);
    }

    private CommunityChannel authorize(Long userId, Long channelId) {
        UserAcademicProfile profile = profileRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new AccessDeniedException("Active academic profile required"));
        CommunityChannel channel = channel(channelId);

        if (!channel.isActive()) {
            throw new BadRequestException("Channel is inactive");
        }

        Long universityId = profile.getUniversity().getId();
        Long collegeId = profile.getCollege().getId();
        Long branchId = profile.getBranch().getId();

        if (channel.getChannelType() == CommunityChannelType.UNIVERSITY_BRANCH) {
            if (channel.getUniversity() == null || channel.getBranch() == null
                    || !universityId.equals(channel.getUniversity().getId())
                    || !branchId.equals(channel.getBranch().getId())
                    || !universityId.equals(channel.getBranch().getUniversity().getId())) {
                throw new AccessDeniedException("You do not have access to this university branch group");
            }
            return channel;
        }

        if (channel.getChannelType() == CommunityChannelType.BRANCH) {
            if (channel.getCollege() == null || channel.getBranch() == null
                    || !collegeId.equals(channel.getCollege().getId())
                    || !branchId.equals(channel.getBranch().getId())
                    || !universityId.equals(channel.getCollege().getUniversity().getId())
                    || !universityId.equals(channel.getBranch().getUniversity().getId())) {
                throw new AccessDeniedException("You do not have access to this college branch group");
            }

            if (!collegeBranchRepository.existsByCollege_IdAndBranch_IdAndActiveTrue(collegeId, branchId)) {
                throw new AccessDeniedException("Branch is not active for this college");
            }
            return channel;
        }

        throw new AccessDeniedException(
                "Only your university branch group and your college branch group are available");
    }

    private CommunityChannel channel(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Community channel not found"));
    }

    private CommunityMessage getMessage(Long messageId, Long channelId) {
        return messageRepository.findByIdAndChannel_Id(messageId, channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    private User user(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && "ADMIN".equals(user.getRole().getName().name());
    }

    private boolean hasExternalLink(String content) {
        String value = content.toLowerCase();
        return value.contains("http://") || value.contains("https://") || value.contains("www.");
    }

    private int boundedSize(int requested) {
        return Math.min(Math.max(1, requested), MAX_PAGE_SIZE);
    }

    private CommunityMessageResponse response(CommunityMessage message) {
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

    private CommunityAttachmentResponse attachmentResponse(CommunityMessageAttachment attachment) {
        CommunityMessage message = attachment.getMessage();
        return CommunityAttachmentResponse.builder()
                .id(attachment.getId())
                .messageId(message.getId())
                .authorName(message.getAuthor().getFullName())
                .fileName(attachment.getFileName())
                .contentType(attachment.getContentType())
                .fileUrl(attachment.getStorageKey())
                .sizeBytes(attachment.getSizeBytes())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
