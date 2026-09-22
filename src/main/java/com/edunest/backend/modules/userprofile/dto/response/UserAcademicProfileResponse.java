package com.edunest.backend.modules.userprofile.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAcademicProfileResponse {

    private Long id;

    private Long userId;
    private String userName;

    private Long universityId;
    private String universityName;

    private Long collegeId;
    private String collegeName;

    private Long branchId;
    private String branchName;

    private Long academicYearId;
    private String academicYearName;

    private Long semesterId;
    private String semesterName;

    private String rollNumber;
    private String division;
    private Integer graduationYear;
    private Double cgpa;
    private Integer backlogCount;

    // IMPORTANT FOR FRONTEND
    private boolean profileCompleted;
}