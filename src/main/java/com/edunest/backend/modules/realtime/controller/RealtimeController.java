package com.edunest.backend.modules.realtime.controller;
import com.edunest.backend.modules.realtime.presence.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
@Controller @RequiredArgsConstructor
public class RealtimeController {
 private final PresenceService presence;
 private final SimpMessagingTemplate messaging;
 @MessageMapping("/presence/online") public void online(Principal principal){presence.online(principal.getName());}
 @MessageMapping("/presence/offline") public void offline(Principal principal){presence.offline(principal.getName());}
 @MessageMapping("/typing") public void typing(Principal principal,@Payload TypingEvent event){
  if(event.channelId()!=null) messaging.convertAndSend("/topic/community/"+event.channelId()+"/typing",new TypingEvent(event.channelId(),event.typing(),principal.getName()));
 }
 public record TypingEvent(Long channelId,boolean typing,String username){
  public TypingEvent(Long channelId,boolean typing){this(channelId,typing,null);}
 }
}