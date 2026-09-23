package com.edunest.backend.modules.college.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeResponse {

    private String id;
    private String name;
    private String code;
    private boolean active;
    private String city;
    private String state;

    private String universityId;
    private String universityName;
}
