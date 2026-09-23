package com.edunest.backend.modules.bookmark.dto.response;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkResponse {

    private Long bookmarkId;
    private Long resourceId;
    private String title;
    private String slug;
    private MaterialType materialType;
    private AccessType accessType;
    private String thumbnailUrl;
    private String coverImageUrl;
    private Double ratingAverage;
    private Integer ratingCount;
    private LocalDateTime bookmarkedAt;

    private boolean resourceActive;
    private boolean resourcePublished;

    private boolean canPreview;
    private boolean canViewFull;
    private boolean canDownload;
    private boolean requiresPurchase;

    private String accessMessage;
    private String viewUrl;
    private String downloadUrl;
}
