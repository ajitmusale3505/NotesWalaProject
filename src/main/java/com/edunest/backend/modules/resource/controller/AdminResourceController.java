package com.edunest.backend.modules.resource.controller;

import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.MaterialType;
import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.resource.dto.request.AdminUploadResourceRequest;
import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import com.edunest.backend.modules.resource.service.ResourceService;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import com.edunest.backend.modules.resource.dto.request.AdminPatchResourceRequest;

import com.edunest.backend.modules.resource.dto.request.AdminPutResourceRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/resources")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminResourceController {

    private final ResourceService resourceService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<ResourceResponse>> uploadResource(
            @RequestPart("pdfFile") MultipartFile pdfFile,
            @RequestPart(value = "previewFile", required = false) MultipartFile previewFile,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,

            @RequestParam String title,
            @RequestParam(required = false) String slug,
            @RequestParam(required = false) String description,

            @RequestParam Long categoryId,
            @RequestParam Long universityId,
            @RequestParam(required = false) Long collegeId,
            @RequestParam Long branchId,
            @RequestParam Long academicYearId,
            @RequestParam Long semesterId,
            @RequestParam Long subjectId,

            @RequestParam MaterialType materialType,
            @RequestParam AccessType accessType,

            @RequestParam BigDecimal price,
            @RequestParam(required = false) BigDecimal discountPrice,

            @RequestParam(required = false) Integer pageCount,
            @RequestParam(required = false) Integer previewPages,

            @RequestParam(required = false) String version,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String tags,

            @RequestParam boolean downloadable,
            @RequestParam boolean watermarkEnabled,
            @RequestParam boolean active,
            @RequestParam boolean published
    ) {

        AdminUploadResourceRequest request =
                AdminUploadResourceRequest.builder()
                        .title(title)
                        .slug(slug)
                        .description(description)
                        .categoryId(categoryId)
                        .universityId(universityId)
                        .collegeId(collegeId)
                        .branchId(branchId)
                        .academicYearId(academicYearId)
                        .semesterId(semesterId)
                        .subjectId(subjectId)
                        .materialType(materialType)
                        .accessType(accessType)
                        .price(price)
                        .discountPrice(discountPrice)
                        .pageCount(pageCount)
                        .previewPages(previewPages)
                        .version(version)
                        .language(language)
                        .tags(tags)
                        .downloadable(downloadable)
                        .watermarkEnabled(watermarkEnabled)
                        .active(active)
                        .published(published)
                        .build();

        ResourceResponse responseData =
                resourceService.adminUploadResource(
                        pdfFile,
                        previewFile,
                        thumbnail,
                        coverImage,
                        request
                );

        ApiResponse<ResourceResponse> response =
                ApiResponse.<ResourceResponse>builder()
                        .success(true)
                        .message("Resource uploaded successfully")
                        .data(responseData)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<ResourceResponse>> patchResource(
            @PathVariable Long resourceId,
            @RequestBody AdminPatchResourceRequest request
    ) {

        ResourceResponse updated =
                resourceService.patchResource(resourceId, request);

        ApiResponse<ResourceResponse> response =
                ApiResponse.<ResourceResponse>builder()
                        .success(true)
                        .message("Resource updated successfully")
                        .data(updated)
                        .build();

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<ResourceResponse>> replaceResource(
            @PathVariable Long resourceId,
            @Valid @RequestBody AdminPutResourceRequest request
    ) {

        ResourceResponse updated =
                resourceService.replaceResource(resourceId, request);

        ApiResponse<ResourceResponse> response =
                ApiResponse.<ResourceResponse>builder()
                        .success(true)
                        .message("Resource replaced successfully")
                        .data(updated)
                        .build();

        return ResponseEntity.ok(response);
    }
    
}