package com.edunest.backend.modules.coupon.service;
import com.edunest.backend.modules.coupon.dto.request.*;
import com.edunest.backend.modules.coupon.dto.response.*;
import org.springframework.data.domain.*;
public interface CouponService {
    CouponResponse create(AdminCreateCouponRequest request);
    CouponResponse update(Long id, AdminCreateCouponRequest request);
    void deactivate(Long id);
    Page<CouponResponse> getAll(Pageable pageable);
    CouponValidationResponse validate(Long userId, CouponValidationRequest request);
    void consumeByCode(String code, Long userId);
}