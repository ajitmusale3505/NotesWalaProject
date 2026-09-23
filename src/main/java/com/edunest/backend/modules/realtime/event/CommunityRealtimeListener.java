package com.edunest.backend.modules.realtime.event;
import com.edunest.backend.modules.communitychat.event.CommunityMessageCreatedEvent;
import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.entity.CommunityMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class CommunityRealtimeListener {
 private final SimpMessagingTemplate messaging;
 @EventListener public void onMessage(CommunityMessageCreatedEvent event){
  CommunityMessage m=event.message();
  messaging.convertAndSend("/topic/community/"+m.getChannel().getId(),CommunityMessageResponse.builder()
   .id(m.getId()).channelId(m.getChannel().getId()).authorId(m.getAuthor().getId()).authorName(m.getAuthor().getFullName())
   .parentMessageId(m.getParentMessage()==null?null:m.getParentMessage().getId()).content(m.getContent()).status(m.getStatus().name())
   .pinned(m.isPinned()).createdAt(m.getCreatedAt()).updatedAt(m.getUpdatedAt()).build());
 }
}