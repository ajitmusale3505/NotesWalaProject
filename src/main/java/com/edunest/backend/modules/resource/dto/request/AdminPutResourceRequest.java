package com.edunest.backend.modules.resource.dto.request;

import java.math.BigDecimal;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.DocumentType;
import com.edunest.backend.common.enums.MaterialType;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPutResourceRequest {

    @NotNull
    private String title;

    @NotNull
    private String slug;

    private String description;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long universityId;

    private Long collegeId;

    @NotNull
    private Long branchId;

    @NotNull
    private Long academicYearId;

    @NotNull
    private Long semesterId;

    @NotNull
    private Long subjectId;

    @NotNull private DocumentType documentType;
    private MaterialType materialType;

    @NotNull
    private AccessType accessType;

    @NotNull
    private BigDecimal price;

    private BigDecimal discountPrice;

    private Integer pageCount;
    private Integer previewPages;

    private String version;
    private String language;
    @Size(max = 2000) private String tags;
    @Size(max = 10000) private String metadata;

    private boolean downloadable;
    private boolean watermarkEnabled;
    private boolean active;
    private boolean published;
}