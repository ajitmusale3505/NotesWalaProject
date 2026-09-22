package com.edunest.backend.modules.order.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.common.enums.OrderType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;

    private String orderNumber;

    private OrderType orderType;

    private OrderStatus status;

    private BigDecimal subtotalAmount;

    private BigDecimal discountAmount;

    private BigDecimal taxAmount;

    private BigDecimal finalAmount;
    
    private String resourceTitle;
    
    private BigDecimal amount;

    private String currency;

    private String couponCode;

    private BigDecimal couponDiscount;

    private boolean paymentRequired;

    private List<OrderItemResponse> items;
}