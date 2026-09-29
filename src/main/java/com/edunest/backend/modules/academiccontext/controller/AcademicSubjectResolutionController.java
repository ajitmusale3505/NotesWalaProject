package com.edunest.backend.modules.academiccontext.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.academiccontext.dto.AcademicSubjectResponse;
import com.edunest.backend.modules.academiccontext.service.AcademicSubjectResolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/academic-context") @RequiredArgsConstructor
public class AcademicSubjectResolutionController {
    private final AcademicSubjectResolutionService service;

    @GetMapping("/context")
    public ResponseEntity<ApiResponse<com.edunest.backend.modules.academiccontext.dto.AcademicContextResponse>> getCurrentUserContext() {
        return ResponseEntity.ok(ApiResponse.<com.edunest.backend.modules.academiccontext.dto.AcademicContextResponse>builder()
                .success(true)
                .message("Academic context resolved successfully")
                .data(service.getCurrentUserContext())
                .build());
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<AcademicSubjectResponse>>> getCurrentUserSubjects() {
        return ResponseEntity.ok(ApiResponse.<List<AcademicSubjectResponse>>builder()
                .success(true)
                .message("Academic subjects resolved successfully")
                .data(service.getCurrentUserSubjects())
                .build());
    }
}
