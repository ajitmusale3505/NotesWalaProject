package com.edunest.backend.modules.userprofile.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAcademicProfilePatchRequest {

    private Long universityId;
    private Long collegeId;
    private Long branchId;
    private Long academicYearId;
    private Long semesterId;

    @Size(max = 50)
    private String rollNumber;

    @Size(max = 20)
    private String division;

    @Min(2020)
    @Max(2100)
    private Integer graduationYear;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double cgpa;

    @Min(0)
    private Integer backlogCount;
}