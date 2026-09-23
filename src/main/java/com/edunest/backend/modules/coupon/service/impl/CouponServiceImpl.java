package com.edunest.backend.modules.coupon.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import com.edunest.backend.common.enums.*;
import com.edunest.backend.common.exception.*;
import com.edunest.backend.modules.coupon.dto.request.*;
import com.edunest.backend.modules.coupon.dto.response.*;
import com.edunest.backend.modules.coupon.entity.*;
import com.edunest.backend.modules.coupon.repository.*;
import com.edunest.backend.modules.coupon.service.CouponService;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import com.edunest.backend.modules.subscription.repository.SubscriptionPlanRepository;
import com.edunest.backend.modules.user.entity.User;
import com.edunest.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CouponUsageRepository usageRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final SubscriptionPlanRepository planRepository;

    @Override @Transactional
    public CouponResponse create(AdminCreateCouponRequest r) {
        validateDefinition(r);
        Coupon c = new Coupon();
        apply(c,r);
        c.setUsedCount(0);
        return toResponse(couponRepository.save(c));
    }

    @Override @Transactional
    public CouponResponse update(Long id, AdminCreateCouponRequest r) {
        Coupon c=get(id); validateDefinition(r);
        if (!c.getCode().equalsIgnoreCase(r.getCode().trim()) && couponRepository.existsByCodeIgnoreCase(r.getCode().trim()))
            throw new BadRequestException("Coupon code already exists");
        apply(c,r);
        return toResponse(couponRepository.save(c));
    }

    @Override @Transactional
    public void deactivate(Long id) { Coupon c=get(id); c.setActive(false); couponRepository.save(c); }

    @Override
    public Page<CouponResponse> getAll(Pageable pageable) { return couponRepository.findAll(pageable).map(this::toResponse); }

    @Override
    public CouponValidationResponse validate(Long userId, CouponValidationRequest r) {
        userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        Coupon c=findCode(r.getCode());
        BigDecimal discount=calculate(c,userId,r.getOrderAmount(),r.getResourceId(),r.getSubscriptionPlanId());
        BigDecimal total=r.getOrderAmount().subtract(discount).max(BigDecimal.ZERO);
        return CouponValidationResponse.builder().code(c.getCode()).valid(true).message("Coupon is valid")
                .orderAmount(r.getOrderAmount()).discountAmount(discount).finalAmount(total).build();
    }

    @Override @Transactional
    public void consumeByCode(String code, Long userId) {
        User u=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        Coupon c=findCode(code);
        calculate(c,userId,BigDecimal.ZERO,null,null);
        if (c.getUsedCount() >= c.getMaxUses()) throw new BadRequestException("Coupon usage limit reached");
        CouponUsage usage=usageRepository.findByCoupon_IdAndUser_Id(c.getId(),userId).orElse(null);
        if (usage!=null && usage.getUsageCount() >= c.getMaxUsesPerUser()) throw new BadRequestException("Coupon per-user usage limit reached");
        c.setUsedCount(c.getUsedCount()+1);
        if (usage==null) usage=CouponUsage.builder().coupon(c).user(u).usageCount(0).build();
        usage.setUsageCount(usage.getUsageCount()+1); usage.setLastUsedAt(LocalDateTime.now());
        couponRepository.save(c); usageRepository.save(usage);
    }

    private BigDecimal calculate(Coupon c, Long userId, BigDecimal amount, Long resourceId, Long planId) {
        LocalDateTime now=LocalDateTime.now();
        if(!c.isActive()) throw new BadRequestException("Coupon is inactive");
        if(c.getStartDate()!=null && now.isBefore(c.getStartDate())) throw new BadRequestException("Coupon is not active yet");
        if(c.getExpiryDate()!=null && !now.isBefore(c.getExpiryDate())) throw new BadRequestException("Coupon has expired");
        if(c.getUsedCount() >= c.getMaxUses()) throw new BadRequestException("Coupon usage limit reached");
        int used=usageRepository.findByCoupon_IdAndUser_Id(c.getId(),userId).map(CouponUsage::getUsageCount).orElse(0);
        if(used>=c.getMaxUsesPerUser()) throw new BadRequestException("Coupon per-user usage limit reached");
        if(amount.compareTo(c.getMinimumOrderAmount())<0 && amount.signum()>0) throw new BadRequestException("Minimum order amount is "+c.getMinimumOrderAmount());
        if(c.getScope()==CouponScope.RESOURCE && (resourceId==null || c.getResource()==null || !c.getResource().getId().equals(resourceId))) throw new BadRequestException("Coupon is not valid for this resource");
        if(c.getScope()==CouponScope.SUBSCRIPTION && (planId==null || c.getSubscriptionPlan()==null || !c.getSubscriptionPlan().getId().equals(planId))) throw new BadRequestException("Coupon is not valid for this subscription plan");
        if(amount.signum()<=0) return BigDecimal.ZERO;
        BigDecimal d=c.getCouponType()==CouponType.PERCENTAGE
                ? amount.multiply(c.getDiscountValue()).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP)
                : c.getDiscountValue();
        return d.min(amount).max(BigDecimal.ZERO);
    }

    private void validateDefinition(AdminCreateCouponRequest r) {
        if(r.getDiscountValue()==null || r.getMinimumOrderAmount()==null || r.getMaxUses()==null || r.getMaxUsesPerUser()==null) throw new BadRequestException("Coupon limits and discount are required");
        if(r.getCouponType()==CouponType.PERCENTAGE && r.getDiscountValue().compareTo(BigDecimal.valueOf(100))>0) throw new BadRequestException("Percentage discount cannot exceed 100");
        if(r.getExpiryDate()!=null && r.getStartDate()!=null && !r.getExpiryDate().isAfter(r.getStartDate())) throw new BadRequestException("Expiry date must be after start date");
        if(r.getScope()==CouponScope.RESOURCE && r.getResourceId()==null) throw new BadRequestException("Resource is required for a resource coupon");
        if(r.getScope()==CouponScope.SUBSCRIPTION && r.getSubscriptionPlanId()==null) throw new BadRequestException("Subscription plan is required for a subscription coupon");
        if(r.getScope()==CouponScope.GENERAL && (r.getResourceId()!=null || r.getSubscriptionPlanId()!=null)) throw new BadRequestException("General coupon cannot have a target");
        if(couponRepository.existsByCodeIgnoreCase(r.getCode().trim())) throw new BadRequestException("Coupon code already exists");
    }

    private void apply(Coupon c, AdminCreateCouponRequest r) {
        c.setCode(r.getCode().trim().toUpperCase()); c.setDescription(r.getDescription()); c.setCouponType(r.getCouponType()); c.setScope(r.getScope());
        c.setDiscountValue(r.getDiscountValue()); c.setMinimumOrderAmount(r.getMinimumOrderAmount()); c.setMaxUses(r.getMaxUses()); c.setMaxUsesPerUser(r.getMaxUsesPerUser());
        c.setStartDate(r.getStartDate()); c.setExpiryDate(r.getExpiryDate()); c.setActive(r.getActive()==null || r.getActive());
        c.setResource(r.getResourceId()==null?null:resourceRepository.findById(r.getResourceId()).orElseThrow(()->new ResourceNotFoundException("Resource not found")));
        c.setSubscriptionPlan(r.getSubscriptionPlanId()==null?null:planRepository.findById(r.getSubscriptionPlanId()).orElseThrow(()->new ResourceNotFoundException("Subscription plan not found")));
    }
    private Coupon findCode(String code){return couponRepository.findByCodeIgnoreCase(code.trim()).orElseThrow(()->new BadRequestException("Invalid coupon code"));}
    private Coupon get(Long id){return couponRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Coupon not found"));}
    private CouponResponse toResponse(Coupon c){return CouponResponse.builder().id(c.getId()).code(c.getCode()).description(c.getDescription()).couponType(c.getCouponType()).scope(c.getScope()).resourceId(c.getResource()==null?null:c.getResource().getId()).subscriptionPlanId(c.getSubscriptionPlan()==null?null:c.getSubscriptionPlan().getId()).discountValue(c.getDiscountValue()).minimumOrderAmount(c.getMinimumOrderAmount()).maxUses(c.getMaxUses()).usedCount(c.getUsedCount()).maxUsesPerUser(c.getMaxUsesPerUser()).startDate(c.getStartDate()).expiryDate(c.getExpiryDate()).active(c.isActive()).build();}
}