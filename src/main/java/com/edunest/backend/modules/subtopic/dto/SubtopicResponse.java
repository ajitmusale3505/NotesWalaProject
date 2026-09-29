package com.edunest.backend.modules.subtopic.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SubtopicResponse {
    String id;
    Integer subtopicNumber;
    String name;
    String description;
    boolean active;
    String topicId;
    String topicName;
    String unitId;
    String subjectId;
}
