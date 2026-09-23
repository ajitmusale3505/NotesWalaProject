package com.edunest.backend.modules.resource.controller;

import java.util.List;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.resource.dto.request.CreateResourceRequest;
import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import com.edunest.backend.modules.resource.service.ResourceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.edunest.backend.security.util.SecurityUtils;

import com.edunest.backend.modules.resource.dto.response.ResourceAccessResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.edunest.backend.modules.storage.dto.FileStreamResponse;
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ResourceResponse>> createResource(
            @Valid @RequestBody CreateResourceRequest request) {

        ResourceResponse resource =
                resourceService.createResource(request);

        ApiResponse<ResourceResponse> response =
                ApiResponse.<ResourceResponse>builder()
                        .success(true)
                        .message("Resource created successfully")
                        .data(resource)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getAllResources() {

        List<ResourceResponse> resources =
                resourceService.getAllResources();

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Resources fetched successfully")
                        .data(resources)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceResponse>> getResourceById(
            @PathVariable Long id) {

        ResourceResponse resource =
                resourceService.getResourceById(id);

        ApiResponse<ResourceResponse> response =
                ApiResponse.<ResourceResponse>builder()
                        .success(true)
                        .message("Resource fetched successfully")
                        .data(resource)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> searchResources(
            @RequestParam String keyword) {

        List<ResourceResponse> resources =
                resourceService.searchResources(keyword);

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Search completed successfully")
                        .data(resources)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getByBranch(
            @PathVariable Long branchId) {

        List<ResourceResponse> resources =
                resourceService.getResourcesByBranch(branchId);

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Branch resources fetched successfully")
                        .data(resources)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getBySemester(
            @PathVariable Long semesterId) {

        List<ResourceResponse> resources =
                resourceService.getResourcesBySemester(semesterId);

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Semester resources fetched successfully")
                        .data(resources)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getBySubject(
            @PathVariable Long subjectId) {

        List<ResourceResponse> resources =
                resourceService.getResourcesBySubject(subjectId);

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Subject resources fetched successfully")
                        .data(resources)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/material/{materialType}")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getByMaterialType(
            @PathVariable MaterialType materialType) {

        List<ResourceResponse> resources =
                resourceService.getResourcesByMaterialType(materialType);

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Material type resources fetched successfully")
                        .data(resources)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/access/{accessType}")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getByAccessType(
            @PathVariable AccessType accessType) {

        List<ResourceResponse> resources =
                resourceService.getResourcesByAccessType(accessType);

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Access type resources fetched successfully")
                        .data(resources)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> filterResources(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) MaterialType materialType) {

        List<ResourceResponse> resources =
                resourceService.filterResources(
                        keyword,
                        branchId,
                        semesterId,
                        subjectId,
                        materialType
                );

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Filtered resources fetched successfully")
                        .data(resources)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{resourceId}/access")
    public ResponseEntity<ApiResponse<ResourceAccessResponse>> checkAccess(
            @PathVariable Long resourceId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ResourceAccessResponse result =
                resourceService.checkAccess(currentUserId, resourceId);

        ApiResponse<ResourceAccessResponse> response =
                ApiResponse.<ResourceAccessResponse>builder()
                        .success(true)
                        .message("Access checked")
                        .data(result)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{resourceId}/preview")
    public ResponseEntity<InputStreamResource> previewResource(
            @PathVariable Long resourceId) {

        FileStreamResponse file =
                resourceService.streamPreview(resourceId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new InputStreamResource(file.getInputStream()));
    }
    
    @GetMapping("/{resourceId}/view")
    public ResponseEntity<InputStreamResource> viewFullResource(
            @PathVariable Long resourceId) {

        FileStreamResponse file =
                resourceService.streamView(SecurityUtils.getCurrentUserId(), resourceId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new InputStreamResource(file.getInputStream()));
    }
    
    @GetMapping("/{resourceId}/download")
    public ResponseEntity<InputStreamResource> downloadResource(
            @PathVariable Long resourceId) {

        FileStreamResponse file =
                resourceService.streamDownload(SecurityUtils.getCurrentUserId(), resourceId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\""
                )
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(file.getContentLength())
                .body(new InputStreamResource(file.getInputStream()));
    }
    
    
    @DeleteMapping("/{resourceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteResource(
            @PathVariable Long resourceId) {

        resourceService.deleteResource(resourceId);

        ApiResponse<String> response =
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Resource deleted successfully")
                        .data("Deleted")
                        .build();

        return ResponseEntity.ok(response);
    }
    
}