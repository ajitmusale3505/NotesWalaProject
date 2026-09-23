package com.edunest.backend.modules.review.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSummaryResponse {

    private Long resourceId;
    private Double ratingAverage;
    private Integer ratingCount;
}
