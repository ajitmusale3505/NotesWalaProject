package com.edunest.backend.modules.coupon.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edunest.backend.modules.coupon.entity.CouponUsage;
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    Optional<CouponUsage> findByCoupon_IdAndUser_Id(Long couponId, Long userId);
}