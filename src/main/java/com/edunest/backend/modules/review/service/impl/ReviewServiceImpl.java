package com.edunest.backend.modules.review.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resourceentitlement.service.UserResourceEntitlementService;
import com.edunest.backend.modules.review.dto.request.CreateReviewRequest;
import com.edunest.backend.modules.review.dto.request.UpdateReviewRequest;
import com.edunest.backend.modules.review.dto.response.ReviewResponse;
import com.edunest.backend.modules.review.dto.response.ReviewSummaryResponse;
import com.edunest.backend.modules.review.service.ReviewService;
import com.edunest.backend.modules.review.entity.Review;
import com.edunest.backend.modules.review.repository.ReviewRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ReviewRepository reviewRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final UserResourceEntitlementService entitlementService;

    @Override
    @Transactional
    public ReviewResponse createReview(Long resourceId, Long userId, CreateReviewRequest request) {
        Resource resource = getResource(resourceId);
        User user = getUser(userId);

        if (!entitlementService.hasActiveEntitlement(userId, resourceId, "PURCHASE")) {
            throw new AccessDeniedException("Only users who purchased the resource can review it");
        }

        if (reviewRepository.existsByResource_IdAndUser_Id(resourceId, userId)) {
            throw new BadRequestException("You have already reviewed this resource");
        }

        Review saved = reviewRepository.save(Review.builder()
                .resource(resource)
                .user(user)
                .rating(request.getRating())
                .review(normalizeReview(request.getReview()))
                .build());

        refreshResourceRating(resourceId);
        return toResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getResourceReviews(Long resourceId, int page, int size) {
        getResource(resourceId);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        return reviewRepository.findByResource_Id(
                        resourceId,
                        PageRequest.of(
                                safePage,
                                safeSize,
                                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))))
                .map(this::toResponse);
    }

    @Override
    public ReviewSummaryResponse getReviewSummary(Long resourceId) {
        getResource(resourceId);

        Double average = reviewRepository.findAverageRating(resourceId);
        long count = reviewRepository.countByResource_Id(resourceId);

        return ReviewSummaryResponse.builder()
                .resourceId(resourceId)
                .ratingAverage(average == null ? 0.0 : roundAverage(average))
                .ratingCount(Math.toIntExact(count))
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(
            Long resourceId,
            Long reviewId,
            Long userId,
            UpdateReviewRequest request) {

        Review review = reviewRepository.findByIdAndResource_Id(reviewId, resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to update this review");
        }

        review.setRating(request.getRating());
        review.setReview(normalizeReview(request.getReview()));

        Review saved = reviewRepository.save(review);
        refreshResourceRating(resourceId);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteReview(Long resourceId, Long reviewId, Long userId, boolean admin) {
        Review review = reviewRepository.findByIdAndResource_Id(reviewId, resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!admin && !review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to delete this review");
        }

        reviewRepository.delete(review);
        reviewRepository.flush();
        refreshResourceRating(resourceId);
    }

    @Override
    public ReviewResponse getMyReview(Long resourceId, Long userId) {
        getResource(resourceId);

        Review review = reviewRepository.findByResource_IdAndUser_Id(resourceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        return toResponse(review);
    }

    private void refreshResourceRating(Long resourceId) {
        Double average = reviewRepository.findAverageRating(resourceId);
        long count = reviewRepository.countByResource_Id(resourceId);

        resourceRepository.updateRatingSummary(
                resourceId,
                average == null ? 0.0 : roundAverage(average),
                Math.toIntExact(count));
    }

    private Resource getResource(Long resourceId) {
        if (resourceId == null) {
            throw new BadRequestException("Resource is required");
        }
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    private User getUser(Long userId) {
        if (userId == null) {
            throw new AccessDeniedException("Authenticated user required");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private String normalizeReview(String review) {
        if (review == null) {
            return null;
        }
        String normalized = review.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private double roundAverage(double average) {
        return Math.round(average * 100.0) / 100.0;
    }

    private ReviewResponse toResponse(Review review) {
        User user = review.getUser();

        return ReviewResponse.builder()
                .id(review.getId())
                .resourceId(review.getResource().getId())
                .userId(user.getId())
                .reviewerName(user.getFullName())
                .rating(review.getRating())
                .review(review.getReview())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}