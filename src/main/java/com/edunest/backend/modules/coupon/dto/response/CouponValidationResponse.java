package com.edunest.backend.modules.coupon.dto.response;
import java.math.BigDecimal;
import lombok.*;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CouponValidationResponse {
    private String code; private boolean valid; private String message; private BigDecimal orderAmount; private BigDecimal discountAmount; private BigDecimal finalAmount;
}