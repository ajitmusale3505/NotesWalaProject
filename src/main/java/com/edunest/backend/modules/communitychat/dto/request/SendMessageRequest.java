package com.edunest.backend.modules.communitychat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    @NotBlank
    @Size(max = 2000)
    private String content;

    @Size(max = 64)
    private String clientMessageId;

    private Long parentMessageId;
}
