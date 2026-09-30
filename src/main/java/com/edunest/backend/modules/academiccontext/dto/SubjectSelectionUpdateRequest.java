package com.edunest.backend.modules.academiccontext.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectSelectionUpdateRequest {

    @NotNull(message = "Subject offering IDs are required")
    @Size(max = 30, message = "You can select at most 30 optional subjects")
    private List<String> subjectOfferingIds;
}
