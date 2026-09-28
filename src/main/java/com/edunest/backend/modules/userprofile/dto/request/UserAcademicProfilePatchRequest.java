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
public class UserAcademicProfilePatchRequest {

    @Size(max = 20, message = "University ID too long")
    private String universityId;

    @Size(max = 20, message = "College ID too long")
    private String collegeId;

    @Size(max = 20, message = "Branch ID too long")
    private String branchId;

    @Size(max = 20, message = "Academic year ID too long")
    private String academicYearId;

    @Size(max = 20, message = "Semester ID too long")
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

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;

    private Gender gender;

    @Min(value = 1, message = "Current year must be at least 1")
    @Max(value = 10, message = "Current year cannot be greater than 10")
    private Integer currentYear;

    @Size(max = 100, message = "Degree too long")
    private String degree;

    @Size(max = 30, message = "Mode too long")
    private String mode;

    @Size(max = 50, message = "Current status too long")
    @Pattern(regexp = "(?i)Pursuing|Completed", message = "Current status must be Pursuing or Completed")
    private String currentStatus;

    @DecimalMin(value = "0.0", message = "Last year SGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "Last year SGPA cannot exceed 10")
    private BigDecimal lastYearSgpa;

    @DecimalMin(value = "0.0", message = "10th percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "10th percentage cannot exceed 100")
    private BigDecimal tenthPercentage;

    @DecimalMin(value = "0.0", message = "12th percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "12th percentage cannot exceed 100")
    private BigDecimal twelfthPercentage;

    @Size(max = 100, message = "Diploma details too long")
    private String diplomaDetails;

    @Size(max = 300, message = "Additional information too long")
    private String additionalInformation;

    @Size(max = 100, message = "State name too long")
    private String state;

    @Size(max = 100, message = "City name too long")
    private String city;
}