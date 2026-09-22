package com.edunest.backend.modules.resource.dto.request;

import java.math.BigDecimal;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUploadResourceRequest {

    @NotBlank @Size(max = 200) private String title;
    private String slug;
    private String description;

    private Long categoryId;
    private Long universityId;
    private Long collegeId;
    private Long branchId;
    private Long academicYearId;
    private Long semesterId;
    private Long subjectId;

    private MaterialType materialType;
    private AccessType accessType;

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