package com.edunest.backend.modules.communitychat.event;

import com.edunest.backend.modules.communitychat.dto.response.CommunityMessageResponse;

public record CommunityMessageCreatedEvent(CommunityMessageResponse message) {}
