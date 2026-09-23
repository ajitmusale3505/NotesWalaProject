package com.edunest.backend.modules.library.dto.response;

import com.edunest.backend.common.enums.MaterialType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryPurchaseItemResponse {

    private Long resourceId;
    private String title;
    private MaterialType materialType;
    private String materialDisplayName;
    private BigDecimal amount;
    private Integer quantity;
}
