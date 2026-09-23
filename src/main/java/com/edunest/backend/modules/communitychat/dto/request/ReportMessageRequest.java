package com.edunest.backend.modules.communitychat.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReportMessageRequest { @NotBlank @Size(max=1000) private String reason; }