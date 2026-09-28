package com.edunest.backend.modules.userprofile.dto.response;

import com.edunest.backend.modules.userprofile.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPersonalProfileResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String country;
    private String state;
    private String city;
    private String address;
    private String aboutMe;
    private Set<String> skills;
    private Set<String> interests;
    private String preferredRole;
    private String preferredLocation;
    private String employmentType;
    private String availability;
}
