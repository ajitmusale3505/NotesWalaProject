package com.edunest.backend.modules.resource.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.resource.dto.response.AcademicResourceResponse;
import com.edunest.backend.modules.resource.service.AcademicResourceDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/academic-resources")
@RequiredArgsConstructor
public class AcademicResourceController {

    private final AcademicResourceDiscoveryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AcademicResourceResponse>>> getCurrentUserResources(
            @RequestParam(required = false) String subjectOfferingId) {
        return ResponseEntity.ok(ApiResponse.<List<AcademicResourceResponse>>builder()
                .success(true)
                .message("Academic resources resolved successfully")
                .data(service.getCurrentUserResources(subjectOfferingId))
                .build());
    }
}
