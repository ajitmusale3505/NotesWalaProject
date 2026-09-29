package com.edunest.backend.modules.subject.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubjectCategoryResponse {
    String id;
    String code;
    String name;
    String description;
    boolean active;
}
