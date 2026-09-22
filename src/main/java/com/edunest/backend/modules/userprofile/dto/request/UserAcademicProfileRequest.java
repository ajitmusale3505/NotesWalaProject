package com.edunest.backend.modules.userprofile.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAcademicProfileRequest {

    // Set by the authenticated controller; never trusted from the client.
    private Long userId;

    @NotNull(message = "University ID is required")
    private Long universityId;

    @NotNull(message = "College ID is required")
    private Long collegeId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotNull(message = "Academic year ID is required")
    private Long academicYearId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @Size(max = 50, message = "Roll number too long")
    private String rollNumber;

    @Size(max = 20, message = "Division too long")
    private String division;

    @Min(value = 2020, message = "Invalid graduation year")
    @Max(value = 2100, message = "Invalid graduation year")
    private Integer graduationYear;

    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10")
    private Double cgpa;

    @Min(value = 0, message = "Backlog count cannot be negative")
    private Integer backlogCount;
}