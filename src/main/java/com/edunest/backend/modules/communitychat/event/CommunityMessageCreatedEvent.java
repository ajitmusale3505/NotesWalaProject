package com.edunest.backend.modules.communitychat.event;
import com.edunest.backend.modules.communitychat.entity.CommunityMessage;
public record CommunityMessageCreatedEvent(CommunityMessage message) {}