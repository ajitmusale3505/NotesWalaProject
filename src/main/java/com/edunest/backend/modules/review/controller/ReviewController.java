package com.edunest.backend.modules.review.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.review.dto.request.CreateReviewRequest;
import com.edunest.backend.modules.review.dto.request.UpdateReviewRequest;
import com.edunest.backend.modules.review.dto.response.ReviewResponse;
import com.edunest.backend.modules.review.dto.response.ReviewSummaryResponse;
import com.edunest.backend.modules.review.service.ReviewService;
import com.edunest.backend.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources/{resourceId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long resourceId,
            @Valid @RequestBody CreateReviewRequest request) {

        ReviewResponse review = reviewService.createReview(
                resourceId, SecurityUtils.getCurrentUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ReviewResponse>builder()
                        .success(true)
                        .message("Review created successfully")
                        .data(review)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @PathVariable Long resourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.<Page<ReviewResponse>>builder()
                .success(true)
                .message("Reviews fetched successfully")
                .data(reviewService.getResourceReviews(resourceId, page, size))
                .build());
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ReviewSummaryResponse>> getSummary(
            @PathVariable Long resourceId) {

        return ResponseEntity.ok(ApiResponse.<ReviewSummaryResponse>builder()
                .success(true)
                .message("Review summary fetched successfully")
                .data(reviewService.getReviewSummary(resourceId))
                .build());
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> getMyReview(
            @PathVariable Long resourceId) {

        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Your review fetched successfully")
                .data(reviewService.getMyReview(
                        resourceId, SecurityUtils.getCurrentUserId()))
                .build());
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long resourceId,
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {

        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Review updated successfully")
                .data(reviewService.updateReview(
                        resourceId, reviewId, SecurityUtils.getCurrentUserId(), request))
                .build());
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long resourceId,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(
                resourceId,
                reviewId,
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.isAdmin());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Review deleted successfully")
                .build());
    }
}