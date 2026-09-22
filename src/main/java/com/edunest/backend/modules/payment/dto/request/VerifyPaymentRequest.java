package com.edunest.backend.modules.payment.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyPaymentRequest {

    @NotNull private Long orderId;

    @NotBlank private String providerPaymentId;

    @NotBlank private String providerOrderId;

    @NotBlank private String providerSignature;
}