package com.edunest.backend.modules.coupon.dto.request;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CouponValidationRequest {
    @NotBlank private String code;
    @NotNull @DecimalMin("0.00") private BigDecimal orderAmount;
    private Long resourceId;
    private Long subscriptionPlanId;
}