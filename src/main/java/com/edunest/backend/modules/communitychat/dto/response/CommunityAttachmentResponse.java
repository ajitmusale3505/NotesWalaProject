package com.edunest.backend.modules.communitychat.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityAttachmentResponse {
    private Long id;
    private Long messageId;
    private String authorName;
    private String fileName;
    private String contentType;
    private String fileUrl;
    private long sizeBytes;
    private LocalDateTime createdAt;
}
