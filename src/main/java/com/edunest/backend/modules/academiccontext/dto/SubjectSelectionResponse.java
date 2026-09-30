package com.edunest.backend.modules.academiccontext.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SubjectSelectionResponse {
    List<String> selectedSubjectOfferingIds;
}
