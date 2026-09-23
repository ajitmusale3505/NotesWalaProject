package com.edunest.backend.modules.coupon.dto.request;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.edunest.backend.common.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminCreateCouponRequest {
    @NotBlank @Size(max=50) private String code;
    @Size(max=500) private String description;
    @NotNull private CouponType couponType;
    @NotNull private CouponScope scope;
    private Long resourceId;
    private Long subscriptionPlanId;
    @NotNull @DecimalMin("0.00") private BigDecimal discountValue;
    private BigDecimal maximumDiscountAmount;
    @NotNull @DecimalMin("0.00") private BigDecimal minimumOrderAmount;
    @NotNull @Min(1) private Integer maxUses;
    @NotNull @Min(1) private Integer maxUsesPerUser;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private Boolean active;
}