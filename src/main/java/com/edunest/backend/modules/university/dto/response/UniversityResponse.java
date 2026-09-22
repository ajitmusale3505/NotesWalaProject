package com.edunest.backend.modules.university.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityResponse {

    private Long id;
    private String name;
    private String shortCode;
    private boolean active;
}