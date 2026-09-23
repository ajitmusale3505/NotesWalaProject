package com.edunest.backend.modules.coupon.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;

import com.edunest.backend.common.enums.CouponScope;
import com.edunest.backend.common.enums.CouponType;
import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.coupon.dto.request.*;
import com.edunest.backend.modules.coupon.dto.response.*;
import com.edunest.backend.modules.coupon.entity.*;
import com.edunest.backend.modules.coupon.repository.*;
import com.edunest.backend.modules.coupon.service.CouponService;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.subscription.entity.SubscriptionPlan;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository usageRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final SubscriptionPlanRepository planRepository;

    @Override
    @Transactional
    public CouponResponse create(AdminCreateCouponRequest request) {
        validateDefinition(request, null);
        Coupon coupon = new Coupon();
        apply(coupon, request);
        coupon.setUsedCount(0);
        return toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public CouponResponse update(Long id, AdminCreateCouponRequest request) {
        Coupon coupon = get(id);
        validateDefinition(request, id);
        apply(coupon, request);
        return toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Coupon coupon = get(id);
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    @Override
    public Page<CouponResponse> getAll(Pageable pageable) {
        return couponRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public CouponValidationResponse validate(Long userId, CouponValidationRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Coupon coupon = findCode(request.getCode());
        BigDecimal discount = calculate(
                coupon,
                userId,
                request.getOrderAmount(),
                request.getResourceId(),
                request.getSubscriptionPlanId());

        return CouponValidationResponse.builder()
                .code(coupon.getCode())
                .valid(true)
                .message("Coupon is valid")
                .orderAmount(request.getOrderAmount())
                .discountAmount(discount)
                .finalAmount(request.getOrderAmount().subtract(discount).max(BigDecimal.ZERO))
                .build();
    }

    @Override
    @Transactional
    public void consumeByCode(String code, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Coupon coupon = findCode(code);
        validateForConsumption(coupon, userId);

        CouponUsage usage = usageRepository.findByCoupon_IdAndUser_Id(coupon.getId(), userId)
                .orElseGet(() -> CouponUsage.builder()
                        .coupon(coupon)
                        .user(user)
                        .usageCount(0)
                        .build());

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        usage.setUsageCount(usage.getUsageCount() + 1);
        usage.setLastUsedAt(LocalDateTime.now());

        couponRepository.save(coupon);
        usageRepository.save(usage);
    }

    private BigDecimal calculate(
            Coupon coupon,
            Long userId,
            BigDecimal amount,
            Long resourceId,
            Long subscriptionPlanId) {

        validateForConsumption(coupon, userId);

        if (amount.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new BadRequestException(
                    "Minimum order amount is " + coupon.getMinimumOrderAmount());
        }

        if (coupon.getScope() == CouponScope.RESOURCE) {
            boolean matches = resourceId != null && coupon.getResources().stream()
                    .anyMatch(resource -> resource.getId().equals(resourceId));
            if (!matches) {
                throw new BadRequestException("Coupon is not valid for this resource");
            }
        }

        if (coupon.getScope() == CouponScope.SUBSCRIPTION) {
            boolean matches = subscriptionPlanId != null && coupon.getSubscriptionPlans().stream()
                    .anyMatch(plan -> plan.getId().equals(subscriptionPlanId));
            if (!matches) {
                throw new BadRequestException("Coupon is not valid for this subscription plan");
            }
        }

        BigDecimal discount = coupon.getCouponType() == CouponType.PERCENTAGE
                ? amount.multiply(coupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : coupon.getDiscountValue();

        if (coupon.getMaximumDiscountAmount() != null) {
            discount = discount.min(coupon.getMaximumDiscountAmount());
        }

        return discount.min(amount).max(BigDecimal.ZERO);
    }

    private void validateForConsumption(Coupon coupon, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        if (!coupon.isActive()) {
            throw new BadRequestException("Coupon is inactive");
        }
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new BadRequestException("Coupon is not active yet");
        }
        if (coupon.getExpiryDate() != null && !now.isBefore(coupon.getExpiryDate())) {
            throw new BadRequestException("Coupon has expired");
        }
        if (coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new BadRequestException("Coupon usage limit reached");
        }

        int userUsage = usageRepository.findByCoupon_IdAndUser_Id(coupon.getId(), userId)
                .map(CouponUsage::getUsageCount)
                .orElse(0);

        if (userUsage >= coupon.getMaxUsesPerUser()) {
            throw new BadRequestException("Coupon per-user usage limit reached");
        }
    }

    private void validateDefinition(AdminCreateCouponRequest request, Long id) {
        if (request.getCouponType() == null || request.getScope() == null) {
            throw new BadRequestException("Coupon type and scope are required");
        }
        if (request.getDiscountValue() == null || request.getDiscountValue().signum() < 0) {
            throw new BadRequestException("Discount value must be non-negative");
        }
        if (request.getCouponType() == CouponType.PERCENTAGE
                && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException("Percentage discount cannot exceed 100");
        }
        if (request.getMinimumOrderAmount() == null || request.getMinimumOrderAmount().signum() < 0) {
            throw new BadRequestException("Minimum order amount must be non-negative");
        }
        if (request.getMaxUses() == null || request.getMaxUses() < 1
                || request.getMaxUsesPerUser() == null || request.getMaxUsesPerUser() < 1) {
            throw new BadRequestException("Coupon usage limits must be at least 1");
        }
        if (request.getExpiryDate() != null && request.getStartDate() != null
                && !request.getExpiryDate().isAfter(request.getStartDate())) {
            throw new BadRequestException("Expiry date must be after start date");
        }

        String code = request.getCode().trim();
        if (id == null && couponRepository.existsByCodeIgnoreCase(code)) {
            throw new BadRequestException("Coupon code already exists");
        }

        if (request.getScope() == CouponScope.RESOURCE && request.getResourceId() == null) {
            throw new BadRequestException("Resource is required for a resource coupon");
        }
        if (request.getScope() == CouponScope.SUBSCRIPTION && request.getSubscriptionPlanId() == null) {
            throw new BadRequestException("Subscription plan is required for a subscription coupon");
        }
        if (request.getScope() == CouponScope.GENERAL
                && (request.getResourceId() != null || request.getSubscriptionPlanId() != null)) {
            throw new BadRequestException("General coupon cannot target a resource or subscription plan");
        }
    }

    private void apply(Coupon coupon, AdminCreateCouponRequest request) {
        coupon.setCode(request.getCode().trim().toUpperCase());
        coupon.setDescription(request.getDescription());
        coupon.setCouponType(request.getCouponType());
        coupon.setScope(request.getScope());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        coupon.setMaxUses(request.getMaxUses());
        coupon.setMaxUsesPerUser(request.getMaxUsesPerUser());
        coupon.setStartDate(request.getStartDate());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setActive(request.getActive() == null || request.getActive());

        coupon.getResources().clear();
        coupon.getSubscriptionPlans().clear();

        if (request.getResourceId() != null) {
            Resource resource = resourceRepository.findById(request.getResourceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
            coupon.getResources().add(resource);
        }

        if (request.getSubscriptionPlanId() != null) {
            SubscriptionPlan plan = planRepository.findById(request.getSubscriptionPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));
            coupon.getSubscriptionPlans().add(plan);
        }
    }

    private Coupon findCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Coupon code is required");
        }
        return couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new BadRequestException("Invalid coupon code"));
    }

    private Coupon get(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
    }

    private CouponResponse toResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .couponType(coupon.getCouponType())
                .scope(coupon.getScope())
                .resourceId(coupon.getResources().stream().findFirst().map(Resource::getId).orElse(null))
                .subscriptionPlanId(coupon.getSubscriptionPlans().stream().findFirst().map(SubscriptionPlan::getId).orElse(null))
                .discountValue(coupon.getDiscountValue())
                .minimumOrderAmount(coupon.getMinimumOrderAmount())
                .maxUses(coupon.getMaxUses())
                .usedCount(coupon.getUsedCount())
                .maxUsesPerUser(coupon.getMaxUsesPerUser())
                .startDate(coupon.getStartDate())
                .expiryDate(coupon.getExpiryDate())
                .active(coupon.isActive())
                .build();
    }
}
