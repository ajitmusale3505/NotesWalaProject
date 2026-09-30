package com.edunest.backend.modules.academiccontext.service;

import com.edunest.backend.modules.academiccontext.dto.SubjectSelectionResponse;
import com.edunest.backend.modules.academiccontext.dto.SubjectSelectionUpdateRequest;

public interface SubjectSelectionService {
    SubjectSelectionResponse getCurrentUserSelections();
    SubjectSelectionResponse updateCurrentUserSelections(SubjectSelectionUpdateRequest request);
}
