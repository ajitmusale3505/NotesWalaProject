package com.edunest.backend.modules.category.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private Integer displayOrder;
    private boolean active;
    private long resourceCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
