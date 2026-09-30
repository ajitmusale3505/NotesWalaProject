package com.edunest.backend.modules.academiccontext.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.academiccontext.dto.SubjectSelectionResponse;
import com.edunest.backend.modules.academiccontext.dto.SubjectSelectionUpdateRequest;
import com.edunest.backend.modules.academiccontext.service.SubjectSelectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/academic-context/subject-selections")
@RequiredArgsConstructor
public class SubjectSelectionController {

    private final SubjectSelectionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<SubjectSelectionResponse>> getCurrentUserSelections() {
        return ResponseEntity.ok(ApiResponse.<SubjectSelectionResponse>builder()
                .success(true)
                .message("Current subject selections resolved successfully")
                .data(service.getCurrentUserSelections())
                .build());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SubjectSelectionResponse>> updateCurrentUserSelections(
            @Valid @RequestBody SubjectSelectionUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.<SubjectSelectionResponse>builder()
                .success(true)
                .message("Current subject selections updated successfully")
                .data(service.updateCurrentUserSelections(request))
                .build());
    }
}
