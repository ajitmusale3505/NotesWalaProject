package com.edunest.backend.modules.communitychat.service;

import com.edunest.backend.modules.communitychat.dto.request.ReportMessageRequest;
import com.edunest.backend.modules.communitychat.dto.request.SendMessageRequest;
import com.edunest.backend.modules.communitychat.dto.response.CommunityAttachmentResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMemberResponse;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.entity.CommunityMessageReport;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityChatService {
    Page<CommunityMessageResponse> history(Long userId, Long channelId, int page, int size);
    Page<CommunityMessageResponse> historyBefore(Long userId, Long channelId, Long beforeId, int size);
    Page<CommunityMessageResponse> thread(Long userId, Long channelId, Long messageId, int page, int size);
    Page<CommunityMemberResponse> members(Long userId, Long channelId, int page, int size);
    CommunityMessageResponse pinned(Long userId, Long channelId);
    List<CommunityAttachmentResponse> attachments(Long userId, Long channelId, int limit);
    CommunityMessageResponse send(Long userId, Long channelId, SendMessageRequest request);
    CommunityAttachmentResponse attach(Long userId, Long channelId, Long messageId, MultipartFile file);
    void delete(Long userId, Long channelId, Long messageId);
    void pin(Long userId, Long channelId, Long messageId, boolean pinned);
    void report(Long userId, Long channelId, Long messageId, ReportMessageRequest request);
    void mute(Long adminUserId, Long channelId, Long userId, long minutes);
    void unmute(Long adminUserId, Long channelId, Long userId);
    Page<CommunityMessageReport> pendingReports(Long adminUserId, int page, int size);
    void resolveReport(Long adminUserId, Long reportId);
    void assertCanAccess(Long userId, Long channelId);
}
