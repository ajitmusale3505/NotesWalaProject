package com.edunest.backend.modules.order.dto.response;

import java.math.BigDecimal;


import com.edunest.backend.common.enums.MaterialType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long resourceId;

    private String title;

    private MaterialType materialType;

    private BigDecimal unitPrice;

    private BigDecimal discountAmount;

    private BigDecimal finalUnitPrice;

    private Integer quantity;

    private BigDecimal lineTotal;
}