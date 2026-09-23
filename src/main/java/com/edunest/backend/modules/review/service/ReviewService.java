package com.edunest.backend.modules.review.service;

import com.edunest.backend.modules.review.dto.request.CreateReviewRequest;
import com.edunest.backend.modules.review.dto.request.UpdateReviewRequest;
import com.edunest.backend.modules.review.dto.response.ReviewResponse;
import com.edunest.backend.modules.review.dto.response.ReviewSummaryResponse;
import org.springframework.data.domain.Page;

public interface ReviewService {

    ReviewResponse createReview(Long resourceId, Long userId, CreateReviewRequest request);

    Page<ReviewResponse> getResourceReviews(Long resourceId, int page, int size);

    ReviewSummaryResponse getReviewSummary(Long resourceId);

    ReviewResponse updateReview(Long resourceId, Long reviewId, Long userId, UpdateReviewRequest request);

    void deleteReview(Long resourceId, Long reviewId, Long userId, boolean admin);

    ReviewResponse getMyReview(Long resourceId, Long userId);
}