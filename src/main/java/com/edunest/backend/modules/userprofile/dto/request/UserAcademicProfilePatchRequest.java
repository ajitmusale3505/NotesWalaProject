package com.edunest.backend.modules.userprofile.dto.request;

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
}