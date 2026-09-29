package com.edunest.backend.modules.program.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProgramResponse {
    String id;
    String name;
    String code;
    String degreeLevel;
    String universityId;
    String universityName;
    boolean active;
}
