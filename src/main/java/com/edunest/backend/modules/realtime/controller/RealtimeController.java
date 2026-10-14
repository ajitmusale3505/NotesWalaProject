package com.edunest.backend.modules.realtime.controller;

import com.edunest.backend.modules.communitychat.service.CommunityChatService;
import com.edunest.backend.modules.realtime.presence.PresenceService;
import com.edunest.backend.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class RealtimeController {

    private final PresenceService presence;
    private final SimpMessagingTemplate messaging;
    private final CommunityChatService communityChatService;

    @MessageMapping("/presence/online")
    public void online(Principal principal) {
        presence.online(principal.getName());
    }

    @MessageMapping("/presence/offline")
    public void offline(Principal principal) {
        presence.offline(principal.getName());
    }

    @MessageMapping("/typing")
    public void typing(Principal principal, @Payload TypingEvent event) {
        if (event.channelId() == null) return;
        Long userId = currentUserId(principal);
        communityChatService.assertCanAccess(userId, event.channelId());
        messaging.convertAndSend(
                "/topic/community/" + event.channelId() + "/typing",
                new TypingEvent(event.channelId(), event.typing(), principal.getName()));
    }

    private Long currentUserId(Principal principal) {
        if (principal instanceof Authentication authentication
                && authentication.getPrincipal() instanceof CustomUserDetails details) {
            return details.getUser().getId();
        }
        throw new AccessDeniedException("Authenticated user required");
    }

    public record TypingEvent(Long channelId, boolean typing, String username) {
        public TypingEvent(Long channelId, boolean typing) {
            this(channelId, typing, null);
        }
    }
}
