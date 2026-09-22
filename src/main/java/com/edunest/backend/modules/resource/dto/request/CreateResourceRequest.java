package com.edunest.backend.modules.resource.dto.request;

import java.math.BigDecimal;


import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateResourceRequest {

    private String title;
    private String slug;
    private String description;
    
    private Long collegeId;

    private Long categoryId;
    private Long universityId;
    private Long branchId;
    private Long academicYearId;
    private Long semesterId;
    private Long subjectId;

    private MaterialType materialType;
    private AccessType accessType;

    private BigDecimal price;
    private BigDecimal discountPrice;

    private String fileKey;
    private String previewKey;

    private Long fileSizeBytes;
    private Integer pageCount;
    private Integer previewPages;

    private String thumbnailUrl;
    private String coverImageUrl;

    private String version;
    private String language;
    private String tags;

    private boolean downloadable;
    private boolean watermarkEnabled;
    private boolean active;
    private boolean published;
}