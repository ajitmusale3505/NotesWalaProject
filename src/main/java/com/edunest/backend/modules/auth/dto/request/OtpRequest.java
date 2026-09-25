package com.edunest.backend.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Purpose is required")
    @Pattern(regexp = "REGISTER|LOGIN", message = "Purpose must be REGISTER or LOGIN")
    private String purpose;

    @Pattern(regexp = "^\\d{6}$", message = "OTP must be a 6-digit code")
    private String otp;
}
