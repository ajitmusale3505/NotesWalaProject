package com.edunest.backend.modules.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotBlank
    @Size(max = 120)
    private String slug;
    @Size(max = 500)
    private String description;
    @Size(max = 100)
    private String icon;
    private Integer displayOrder;
}