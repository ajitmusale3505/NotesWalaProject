package com.edunest.backend.modules.elective.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ElectiveGroupResponse {
    String id;
    String curriculumSemesterId;
    String code;
    String name;
    String description;
    Integer requiredSelections;
    Integer availableSubjects;
    Integer displayOrder;
    boolean active;
    List<ElectiveSubjectResponse> subjects;
}
