package com.edunest.backend.modules.communitychat.service;
import com.edunest.backend.modules.communitychat.dto.request.*;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.entity.CommunityChannelType;
import org.springframework.data.domain.Page;
public interface CommunityChatService {
 Page<CommunityMessageResponse> history(Long userId,Long channelId,int page,int size);
 Page<CommunityMessageResponse> thread(Long userId,Long channelId,Long messageId,int page,int size);
 CommunityMessageResponse send(Long userId,Long channelId,SendMessageRequest request);
 void delete(Long userId,Long channelId,Long messageId);
 void pin(Long userId,Long channelId,Long messageId,boolean pinned);
 void report(Long userId,Long channelId,Long messageId,ReportMessageRequest request);
 void mute(Long adminUserId,Long channelId,Long userId,long minutes);
 void unmute(Long adminUserId,Long channelId,Long userId);
}