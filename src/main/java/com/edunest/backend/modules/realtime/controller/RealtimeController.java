package com.edunest.backend.modules.realtime.controller;
import com.edunest.backend.modules.realtime.presence.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import java.security.Principal;
@Controller @RequiredArgsConstructor
public class RealtimeController {
 private final PresenceService presence;
 @MessageMapping("/presence/online") public void online(Principal principal){presence.online(Long.valueOf(principal.getName()));}
 @MessageMapping("/presence/offline") public void offline(Principal principal){presence.offline(Long.valueOf(principal.getName()));}
 @MessageMapping("/typing") public void typing(Principal principal,@Payload TypingEvent event){}
 public record TypingEvent(Long channelId,boolean typing){}
}