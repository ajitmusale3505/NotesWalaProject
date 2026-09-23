package com.edunest.backend.modules.coupon.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.edunest.backend.common.enums.*;
import lombok.*;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CouponResponse {
    private Long id; private String code; private String description; private CouponType couponType; private CouponScope scope;
    private Long resourceId; private Long subscriptionPlanId; private BigDecimal discountValue; private BigDecimal minimumOrderAmount;
    private Integer maxUses; private Integer usedCount; private Integer maxUsesPerUser; private LocalDateTime startDate; private LocalDateTime expiryDate; private boolean active;
}