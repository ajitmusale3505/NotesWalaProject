package com.edunest.backend.modules.subject.dto;

import com.edunest.backend.common.enums.ExamType;
import com.edunest.backend.common.enums.SubjectCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResponseDto {

    private String id;
    private String name;
    private String code;
    private boolean active;

    private SubjectCategory subjectCategory;
    private ExamType examType;

    private Integer credits;

    private String branchId;
    private String branchName;

    private String semesterId;
    private String semesterName;

    private String academicYearId;
    private String academicYearName;
}
