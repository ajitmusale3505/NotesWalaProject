package com.edunest.backend.modules.communitychat.event;

import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
import com.edunest.backend.modules.communitychat.entity.CommunityMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CommunityMessageBroadcastListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void broadcastAfterCommit(CommunityMessageCreatedEvent event) {
        CommunityMessage message = event.message();
        CommunityMessageResponse response = CommunityMessageResponse.builder()
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .authorId(message.getAuthor().getId())
                .authorName(message.getAuthor().getFullName())
                .parentMessageId(message.getParentMessage() == null ? null : message.getParentMessage().getId())
                .content(message.getContent())
                .status(message.getStatus().name())
                .pinned(message.isPinned())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/community/" + response.getChannelId() + "/messages",
                response
        );
    }
}
