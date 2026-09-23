package com.edunest.backend.modules.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    /**
     * Kept for backward compatibility with the existing frontend DTO.
     * The controller overwrites it with the JWT user id.
     */
    private Long userId;

    @NotNull
    private Long resourceId;
    private String couponCode;
}
