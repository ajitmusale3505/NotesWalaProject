package com.edunest.backend.modules.userprofile.dto.request;

import com.edunest.backend.modules.userprofile.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPersonalProfileRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Indian phone number is required")
    @Pattern(regexp = "^[6-9]\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State name too long")
    private String state;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name too long")
    private String city;
}
