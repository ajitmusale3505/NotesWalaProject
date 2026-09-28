package com.edunest.backend.modules.userprofile.dto.request;

import com.edunest.backend.modules.userprofile.enums.Gender;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAcademicProfileRequest {

    @NotBlank(message = "University ID is required")
    private String universityId;

    @NotBlank(message = "College ID is required")
    private String collegeId;

    @NotBlank(message = "Branch ID is required")
    private String branchId;

    @NotBlank(message = "Academic year ID is required")
    private String academicYearId;

    @NotBlank(message = "Semester ID is required")
    private String semesterId;

    @Size(max = 50, message = "Roll number too long")
    @Pattern(regexp = ".*\\S.*", message = "Roll number cannot be blank")
    private String rollNumber;

    @Size(max = 20, message = "Division too long")
    @Pattern(regexp = ".*\\S.*", message = "Division cannot be blank")
    private String division;

    @Min(value = 2020, message = "Invalid graduation year")
    @Max(value = 2100, message = "Invalid graduation year")
    private Integer graduationYear;

    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10")
    @Digits(integer = 2, fraction = 2, message = "CGPA can have at most 2 decimal places")
    private BigDecimal cgpa;

    @Min(value = 0, message = "Backlog count cannot be negative")
    @Max(value = 100, message = "Backlog count is too large")
    private Integer backlogCount;

    @NotBlank(message = "Indian phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Current year is required")
    @Min(value = 1, message = "Current year must be at least 1")
    @Max(value = 10, message = "Current year cannot be greater than 10")
    private Integer currentYear;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State name too long")
    private String state;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name too long")
    private String city;
}