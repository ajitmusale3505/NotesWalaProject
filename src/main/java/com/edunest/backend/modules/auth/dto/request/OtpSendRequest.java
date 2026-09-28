package com.edunest.backend.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpSendRequest {

    @Schema(description = "Email address that should receive the OTP",
            example = "user@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
