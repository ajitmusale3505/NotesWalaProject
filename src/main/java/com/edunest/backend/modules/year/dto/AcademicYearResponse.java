package com.edunest.backend.modules.year.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicYearResponse {

    private String id;
    private String name;
    private String code;
    private Integer startYear;
    private Integer endYear;
    private boolean active;
}
