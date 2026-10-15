package com.edunest.backend.modules.realtime.security;

import com.edunest.backend.modules.communitychat.service.CommunityChatService;
import com.edunest.backend.security.jwt.JwtService;
import com.edunest.backend.security.principal.CustomUserDetails;
import com.edunest.backend.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class JwtStompInterceptor implements ChannelInterceptor {

    private static final Pattern COMMUNITY_DESTINATION =
            Pattern.compile("^/topic/community/(\\d+)/(messages|typing)$");

    private final JwtService jwtService;
    private final CustomUserDetailsService users;
    private final CommunityChatService communityChatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String tokenHeader = accessor.getFirstNativeHeader("Authorization");
            if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("WebSocket authentication required");
            }

            String token = tokenHeader.substring(7).trim();
            String email = jwtService.extractUsername(token);
            UserDetails details = users.loadUserByUsername(email);
            if (!jwtService.isTokenValid(token, details)) {
                throw new IllegalArgumentException("Invalid access token");
            }

            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    details, null, details.getAuthorities()));
            accessor.setLeaveMutable(true);
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        // Clients may subscribe to the community feed, but never publish directly to broker topics.
        if (StompCommand.SEND.equals(command)) {
            String destination = accessor.getDestination();
            if (destination != null && (destination.startsWith("/topic/") || destination.startsWith("/queue/"))) {
                throw new AccessDeniedException("Publishing directly to broker destinations is not allowed");
            }
        }

        if (StompCommand.SUBSCRIBE.equals(command)) {
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/topic/community/")) {
                Matcher matcher = COMMUNITY_DESTINATION.matcher(destination);
                if (!matcher.matches()) {
                    throw new AccessDeniedException("Invalid community subscription destination");
                }

                Principal principal = accessor.getUser();
                if (!(principal instanceof Authentication authentication)
                        || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
                    throw new AccessDeniedException("Authenticated user required");
                }

                communityChatService.assertCanAccess(details.getUser().getId(), Long.valueOf(matcher.group(1)));
            }
        }

        return message;
    }
}
