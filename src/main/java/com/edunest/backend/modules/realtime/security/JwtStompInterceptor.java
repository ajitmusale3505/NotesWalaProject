package com.edunest.backend.modules.realtime.security;
import com.edunest.backend.security.jwt.JwtService;
import com.edunest.backend.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor
public class JwtStompInterceptor implements ChannelInterceptor {
 private final JwtService jwtService; private final CustomUserDetailsService users;
 @Override public Message<?> preSend(Message<?> message,MessageChannel channel){
  StompHeaderAccessor a=StompHeaderAccessor.wrap(message);
  if(StompCommand.CONNECT.equals(a.getCommand())){
   String token=a.getFirstNativeHeader("Authorization");
   if(token==null||!token.startsWith("Bearer ")) throw new IllegalArgumentException("WebSocket authentication required");
   token=token.substring(7).trim();
   String email=jwtService.extractUsername(token);
   UserDetails details=users.loadUserByUsername(email);
   if(!jwtService.isTokenValid(token,details)) throw new IllegalArgumentException("Invalid access token");
   a.setUser(new UsernamePasswordAuthenticationToken(details,null,details.getAuthorities()));
  }
  return message;
 }
}