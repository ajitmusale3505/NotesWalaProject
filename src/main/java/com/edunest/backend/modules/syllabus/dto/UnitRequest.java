package com.edunest.backend.modules.syllabus.dto;

import com.edunest.backend.modules.unit.enums.UnitCoverage;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnitRequest {
    @NotBlank private String subjectId;
    private String subjectOfferingId;
    @NotNull @Positive private Integer unitNumber;
    @NotBlank @Size(max=200) private String chapterName;
    @Size(max=5000) private String description;
    @NotNull private UnitCoverage coverage;
}