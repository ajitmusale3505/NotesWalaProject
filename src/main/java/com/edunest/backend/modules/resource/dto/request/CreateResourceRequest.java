package com.edunest.backend.modules.resource.dto.request;

import java.math.BigDecimal;


import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.DocumentType;
import com.edunest.backend.common.enums.MaterialType;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateResourceRequest {

    @NotBlank @Size(max = 200)
    private String title;
    private String slug;
    private String description;
    
    private Long collegeId;

    @NotNull private Long categoryId;
    private Long universityId;
    @NotNull private Long branchId;
    @NotNull private Long academicYearId;
    @NotNull private Long semesterId;
    @NotNull private Long subjectId;

    @NotNull private MaterialType materialType;
    @NotNull private AccessType accessType;

    @NotNull @DecimalMin("0.00") private BigDecimal price;
    @DecimalMin("0.00") private BigDecimal discountPrice;

    @NotBlank @Size(max = 500)
    private String fileKey;
    private String previewKey;

    private Long fileSizeBytes;
    private Integer pageCount;
    private Integer previewPages;

    private String thumbnailUrl;
    private String coverImageUrl;

    private String version;
    private String language;
    @Size(max = 2000) private String tags;
    @Size(max = 10000) private String metadata;

    private boolean downloadable;
    private boolean watermarkEnabled;
    private boolean active = true;
    private boolean published = false;
}