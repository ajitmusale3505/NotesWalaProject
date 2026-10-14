package com.edunest.backend.modules.communitychat.event;

import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;
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
        CommunityMessageResponse message = event.message();
        messagingTemplate.convertAndSend(
                "/topic/community/" + message.getChannelId() + "/messages",
                message
        );
    }
}
