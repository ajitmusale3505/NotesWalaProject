package com.edunest.backend.modules.resource.dto.response;

import java.math.BigDecimal;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceResponse {

    private Long id;

    private String title;
    private String slug;
    private String description;

    // Academic Hierarchy
    private String categoryName;
    private String branchName;
    private Integer semesterNumber;
    private String subjectName;
    
    private String collegeName;

    // Type
    private MaterialType materialType;
    private AccessType accessType;

    // Pricing
    private BigDecimal price;
    private BigDecimal discountPrice;
    private boolean free;
    private boolean discounted;

    // Preview
    private Integer previewPages;
    private Integer pageCount;
    private Long fileSizeBytes;

    private String previewUrl;
    private String viewUrl;
    private String downloadUrl;
    private String signedPreviewUrl;

    // Images
    private String thumbnailUrl;
    private String coverImageUrl;

    // Metadata
    private String version;
    private String language;
    private String tags;
    private String metadata;

    // Access state
    private boolean purchased;
    private boolean downloadable;

    // Storage Keys
    @JsonIgnore
    private String fileKey;

    // Analytics
    private Long downloadsCount;
    private Double ratingAverage;
    private Integer ratingCount;
    private Double popularityScore;

    // Admin
    private String uploadedByName;
}