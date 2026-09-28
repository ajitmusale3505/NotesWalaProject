package com.edunest.backend.modules.userprofile.dto.request;

import com.edunest.backend.modules.userprofile.enums.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPersonalProfilePatchRequest {

    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @Pattern(regexp = "^$|^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @Size(max = 100, message = "State name too long")
    private String state;

    @Size(max = 100, message = "City name too long")
    private String city;

    @Size(max = 500, message = "About Me cannot exceed 500 characters")
    private String aboutMe;

    @Size(max = 30, message = "You can add at most 30 skills")
    private List<@Size(max = 100, message = "Skill cannot exceed 100 characters") String> skills;

    @Size(max = 30, message = "You can add at most 30 interests")
    private List<@Size(max = 100, message = "Interest cannot exceed 100 characters") String> interests;

    @Size(max = 100, message = "Preferred role cannot exceed 100 characters")
    private String preferredRole;

    @Size(max = 200, message = "Preferred location cannot exceed 200 characters")
    private String preferredLocation;

    @Size(max = 100, message = "Employment type cannot exceed 100 characters")
    private String employmentType;

    @Size(max = 150, message = "Availability cannot exceed 150 characters")
    private String availability;
}
