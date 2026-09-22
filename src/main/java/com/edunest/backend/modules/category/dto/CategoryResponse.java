package com.edunest.backend.modules.category.dto;

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
}