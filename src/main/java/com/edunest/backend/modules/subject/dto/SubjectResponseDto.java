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

    private Long id;
    private String name;
    private String code;
    private boolean active;

    private SubjectCategory subjectCategory;
    private ExamType examType;

    private Integer credits;

    private Long branchId;
    private String branchName;

    private Long semesterId;
    private String semesterName;

    private Long academicYearId;
    private String academicYearName;
}