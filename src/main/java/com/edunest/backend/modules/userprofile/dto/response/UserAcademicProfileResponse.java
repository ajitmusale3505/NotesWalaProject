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
    private String universityId;
    private String universityName;
    private String collegeId;
    private String collegeName;
    private String branchId;
    private String branchName;
    private String academicYearId;
    private String academicYearName;
    private String semesterId;
    private String semesterName;
    private String rollNumber;
    private String division;
    private Integer graduationYear;
    private Double cgpa;
    private Integer backlogCount;
    private int profileCompletionPercentage;
    private boolean profileCompleted;
}