package com.edunest.backend.modules.library.dto.response;

import com.edunest.backend.common.enums.OrderStatus;
import com.edunest.backend.common.enums.OrderType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryPurchaseResponse {

    private Long orderId;
    private String orderNumber;
    private OrderType orderType;
    private OrderStatus status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime purchasedAt;
    private List<LibraryPurchaseItemResponse> items;
}
