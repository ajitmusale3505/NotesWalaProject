package com.edunest.backend.modules.communitychat.dto.response;
import lombok.*;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityAttachmentResponse { private Long id; private String fileName,contentType,fileUrl; private long sizeBytes; }