package com.edunest.backend.modules.review.service.impl;

import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resourceentitlement.service.UserResourceEntitlementService;
import com.edunest.backend.modules.review.dto.request.CreateReviewRequest;
import com.edunest.backend.modules.review.dto.response.ReviewResponse;
import com.edunest.backend.modules.review.entity.Review;
import com.edunest.backend.modules.review.repository.ReviewRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserResourceEntitlementService entitlementService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Resource resource;
    private User user;

    @BeforeEach
    void setUp() {
        resource = Resource.builder()
                .id(10L)
                .title("Test Resource")
                .ratingAverage(0.0)
                .ratingCount(0)
                .build();

        user = User.builder()
                .id(20L)
                .fullName("Test User")
                .build();
    }

    @Test
    void createReviewRequiresPurchaseEntitlement() {
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(entitlementService.hasActiveEntitlement(20L, 10L, "PURCHASE")).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> reviewService.createReview(
                10L, 20L, CreateReviewRequest.builder().rating(5).review("Useful").build()));

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReviewUpdatesResourceRatingSummary() {
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(entitlementService.hasActiveEntitlement(20L, 10L, "PURCHASE")).thenReturn(true);
        when(reviewRepository.existsByResource_IdAndUser_Id(10L, 20L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setId(1L);
            return review;
        });
        when(reviewRepository.findAverageRating(10L)).thenReturn(4.5);
        when(reviewRepository.countByResource_Id(10L)).thenReturn(2L);

        ReviewResponse response = reviewService.createReview(
                10L, 20L,
                CreateReviewRequest.builder().rating(5).review("Useful").build());

        assertEquals(1L, response.getId());
        assertEquals(10L, response.getResourceId());
        assertEquals(20L, response.getUserId());
        assertEquals(5, response.getRating());
        verify(resourceRepository).updateRatingSummary(10L, 4.5, 2);
    }
}