package com.edunest.backend.modules.communitychat.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityMessageResponse {
    private Long id;
    private Long channelId;
    private Long authorId;
    private Long parentMessageId;
    private String clientMessageId;
    private String authorName;
    private String content;
    private String status;
    private boolean pinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
