package com.edunest.backend.modules.admin.service.impl;

import com.edunest.backend.common.enums.RoleType;
import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.admin.dto.*;
import com.edunest.backend.modules.admin.service.AdminCmsService;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.notification.repository.NotificationRepository;
import com.edunest.backend.modules.order.repository.OrderRepository;
import com.edunest.backend.modules.payment.repository.PaymentRepository;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.resourceanalytics.repository.ResourceAnalyticsRepository;
import com.edunest.backend.modules.review.repository.ReviewRepository;
import com.edunest.backend.modules.role.entity.Role;
import com.edunest.backend.modules.role.repository.RoleRepository;
import com.edunest.backend.modules.subject.repository.SubjectRepository;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;
import com.edunest.backend.modules.subscription.repository.SubscriptionRepository;
import com.edunest.backend.modules.topic.repository.TopicRepository;
import com.edunest.backend.modules.unit.repository.UnitRepository;
import com.edunest.backend.modules.university.repository.UniversityRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import com.edunest.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCmsServiceImpl implements AdminCmsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UniversityRepository universityRepository;
    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;
    private final UnitRepository unitRepository;
    private final TopicRepository topicRepository;
    private final ResourceRepository resourceRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final NotificationRepository notificationRepository;
    private final ReviewRepository reviewRepository;
    private final ResourceAnalyticsRepository resourceAnalyticsRepository;

    @Override
    public AdminCmsOverviewResponse getOverview() {
        return AdminCmsOverviewResponse.builder()
                .users(userRepository.count())
                .admins(userRepository.countByRole_Name(RoleType.ADMIN))
                .contributors(userRepository.countByRole_Name(RoleType.CONTRIBUTOR))
                .universities(universityRepository.count())
                .colleges(collegeRepository.count())
                .branches(branchRepository.count())
                .subjects(subjectRepository.count())
                .units(unitRepository.count())
                .topics(topicRepository.count())
                .resources(resourceRepository.count())
                .activeResources(resourceRepository.countByActiveTrue())
                .publishedResources(resourceRepository.countByPublishedTrue())
                .orders(orderRepository.count())
                .payments(paymentRepository.count())
                .subscriptions(subscriptionRepository.count())
                .subscriptionPlans(subscriptionPlanRepository.count())
                .notifications(notificationRepository.count())
                .reviews(reviewRepository.count())
                .analyticsEvents(resourceAnalyticsRepository.count())
                .resourceManagement(true)
                .curriculumManagement(true)
                .notificationManagement(true)
                .orderManagement(true)
                .paymentManagement(true)
                .subscriptionManagement(true)
                .analyticsManagement(true)
                .latexTemplateManagement(false)
                .practicalManualManagement(false)
                .practicalCodeManagement(false)
                .chatModeration(false)
                .build();
    }

    @Override
    public Page<AdminUserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(Long userId, AdminUserUpdateRequest request) {
        if (userId == null || request == null || request.getRole() == null || request.getEnabled() == null) {
            throw new BadRequestException("User update data is required");
        }

        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (target.getId().equals(currentUserId)
                && (request.getEnabled() == false || request.getRole() != RoleType.ADMIN)) {
            throw new BadRequestException("An administrator cannot disable or demote their own account");
        }

        if (target.getRole().getName() == RoleType.ADMIN
                && request.getRole() != RoleType.ADMIN
                && userRepository.countByRole_Name(RoleType.ADMIN) <= 1) {
            throw new BadRequestException("The last administrator cannot be demoted");
        }

        if (request.getRole() == RoleType.ADMIN
                && !target.getRole().getName().equals(RoleType.ADMIN)) {
            Role role = roleRepository.findByName(RoleType.ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("ADMIN role not found"));
            target.setRole(role);
        } else if (!target.getRole().getName().equals(request.getRole())) {
            Role role = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new ResourceNotFoundException("Requested role not found"));
            target.setRole(role);
        }

        target.setEnabled(request.getEnabled());
        return toResponse(userRepository.save(target));
    }

    private AdminUserResponse toResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}