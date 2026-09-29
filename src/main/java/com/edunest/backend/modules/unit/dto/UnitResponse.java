package com.edunest.backend.modules.unit.dto;

import com.edunest.backend.modules.unit.enums.UnitCoverage;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnitResponse {
    private String id;
    private Integer unitNumber;
    private String chapterName;
    private String description;
    private UnitCoverage coverage;
    private boolean active;
    private String subjectId;
    private String subjectName;
    private String subjectCode;
    private String subjectOfferingId;
}