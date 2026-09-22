package com.edunest.backend.modules.semester.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterResponseDto {

    private Long id;
    private Integer number;
    private String name;
    private boolean active;

    private Long academicYearId;
    private String academicYearName;
}