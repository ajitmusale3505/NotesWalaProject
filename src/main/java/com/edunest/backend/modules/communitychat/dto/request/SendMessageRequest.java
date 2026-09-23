package com.edunest.backend.modules.communitychat.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SendMessageRequest {
 @NotBlank @Size(max=2000) private String content;
 private Long parentMessageId;
}