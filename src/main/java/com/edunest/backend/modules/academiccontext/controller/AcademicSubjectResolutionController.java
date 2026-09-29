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

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<AcademicSubjectResponse>>> getCurrentUserSubjects() {
        return ResponseEntity.ok(ApiResponse.<List<AcademicSubjectResponse>>builder()
                .success(true)
                .message("Academic subjects resolved successfully")
                .data(service.getCurrentUserSubjects())
                .build());
    }
}
