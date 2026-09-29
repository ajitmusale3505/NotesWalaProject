package com.edunest.backend.modules.userprofile.dto.response;

import com.edunest.backend.modules.userprofile.enums.Gender;
import java.math.BigDecimal;
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
    private String programId;
    private String programName;
    private String examPatternId;
    private String examPatternName;
    private String academicYearId;
    private String academicYearName;
    private String semesterId;
    private String semesterName;
    private String rollNumber;
    private String division;
    private Integer graduationYear;
    private BigDecimal cgpa;
    private Integer backlogCount;
    private String phoneNumber;
    private Gender gender;
    private Integer currentYear;
    private String degree;
    private String mode;
    private String currentStatus;
    private BigDecimal lastYearSgpa;
    private BigDecimal tenthPercentage;
    private BigDecimal twelfthPercentage;
    private String diplomaDetails;
    private String additionalInformation;
    private String country;
    private String state;
    private String city;
    private int profileCompletionPercentage;
    private boolean profileCompleted;
}