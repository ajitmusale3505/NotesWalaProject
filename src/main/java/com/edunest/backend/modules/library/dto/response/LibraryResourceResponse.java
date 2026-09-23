package com.edunest.backend.modules.library.dto.response;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryResourceResponse {

    private Long resourceId;
    private String title;
    private String slug;
    private MaterialType materialType;
    private String materialDisplayName;
    private String categoryName;
    private String subjectName;
    private String branchName;
    private Integer semesterNumber;
    private AccessType accessType;

    private BigDecimal price;
    private BigDecimal purchasedPrice;

    private String thumbnailUrl;
    private String coverImageUrl;

    private boolean canPreview;
    private boolean canViewFull;
    private boolean canDownload;
    private String accessStatus;

    private Long orderId;
    private String orderNumber;
    private LocalDateTime purchasedAt;

    private LocalDateTime entitlementExpiresAt;
    private LocalDateTime lastDownloadedAt;
}
