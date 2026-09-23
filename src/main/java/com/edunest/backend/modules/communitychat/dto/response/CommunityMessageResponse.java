package com.edunest.backend.modules.communitychat.dto.response;
import java.time.LocalDateTime;
import lombok.*;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityMessageResponse {
 private Long id,channelId,authorId,parentMessageId;
 private String authorName,content,status;
 private boolean pinned;
 private LocalDateTime createdAt,updatedAt;
}