package com.edunest.backend.modules.resource.dto.response;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.DocumentType;
import com.edunest.backend.common.enums.MaterialType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AcademicResourceResponse {
    Long resourceId;
    String title;
    String slug;
    String description;
    String subjectOfferingId;
    String subjectId;
    String subjectCode;
    String subjectName;
    String categoryId;
    String categoryCode;
    String categoryName;
    Integer credits;
    MaterialType materialType;
    DocumentType documentType;
    AccessType accessType;
    BigDecimal price;
    BigDecimal discountPrice;
    boolean downloadable;
    boolean watermarkEnabled;
    String thumbnailUrl;
    String coverImageUrl;
    String language;
    Double ratingAverage;
    Integer ratingCount;
}
