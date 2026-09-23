package com.edunest.backend.modules.resource.controller;

import java.util.List;
import java.math.BigDecimal;
import com.edunest.backend.common.enums.AccessType;
import com.edunest.backend.common.enums.DocumentType;
import com.edunest.backend.common.enums.MaterialType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.resource.dto.response.ResourceResponse;
import com.edunest.backend.modules.resource.service.ResourceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public/resources")
@RequiredArgsConstructor
public class PublicResourceController {

    private final ResourceService resourceService;

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ResourceResponse>>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<ResourceResponse>>builder()
                .success(true)
                .message("Public resources fetched successfully")
                .data(resourceService.getPublicResourcesPage(page, size))
                .build());
    }

    @GetMapping("/search/page")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ResourceResponse>>> searchPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<ResourceResponse>>builder()
                .success(true)
                .message("Search completed successfully")
                .data(resourceService.searchPublicResourcesPage(keyword, page, size))
                .build());
    }


    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ResourceResponse>>> recent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<ResourceResponse>>builder()
                .success(true)
                .message("Recent resources fetched successfully")
                .data(resourceService.getRecentPublicResourcesPage(page, size))
                .build());
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ResourceResponse>>> popular(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<ResourceResponse>>builder()
                .success(true)
                .message("Popular resources fetched successfully")
                .data(resourceService.getPopularPublicResourcesPage(page, size))
                .build());
    }

    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ResourceResponse>>> recommended(
            @RequestParam(required = false) Long universityId,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) MaterialType materialType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<ResourceResponse>>builder()
                .success(true)
                .message("Recommended resources fetched successfully")
                .data(resourceService.getRecommendedPublicResourcesPage(
                        universityId,
                        branchId,
                        academicYearId,
                        semesterId,
                        subjectId,
                        materialType,
                        page,
                        size))
                .build());
    }



    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ResourceResponse>>> filter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long universityId,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) DocumentType documentType,
            @RequestParam(required = false) MaterialType materialType,
            @RequestParam(required = false) AccessType accessType,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "false") boolean freeOnly,
            @RequestParam(defaultValue = "false") boolean discountedOnly,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        var result = resourceService.filterPublicResources(
                keyword, documentType, categoryId, universityId, collegeId, branchId, academicYearId,
                semesterId, subjectId, materialType, accessType, language,
                freeOnly, discountedOnly, minPrice, maxPrice, page, size, sort, direction);

        return ResponseEntity.ok(ApiResponse.<org.springframework.data.domain.Page<ResourceResponse>>builder()
                .success(true)
                .message("Resources filtered successfully")
                .data(result)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getAll() {

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Public resources fetched successfully")
                        .data(resourceService.getPublicResources())
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> search(
            @RequestParam String keyword) {

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Search completed successfully")
                        .data(resourceService.searchPublicResources(keyword))
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> byBranch(
            @PathVariable Long branchId) {

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Branch resources fetched successfully")
                        .data(resourceService.getPublicResourcesByBranch(branchId))
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> bySemester(
            @PathVariable Long semesterId) {

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Semester resources fetched successfully")
                        .data(resourceService.getPublicResourcesBySemester(semesterId))
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> bySubject(
            @PathVariable Long subjectId) {

        ApiResponse<List<ResourceResponse>> response =
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Subject resources fetched successfully")
                        .data(resourceService.getPublicResourcesBySubject(subjectId))
                        .build();

        return ResponseEntity.ok(response);
    }
}