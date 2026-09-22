package com.edunest.backend.modules.college.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeResponse {

    private Long id;
    private String name;
    private String code;
    private boolean active;

    private Long universityId;
    private String universityName;
}