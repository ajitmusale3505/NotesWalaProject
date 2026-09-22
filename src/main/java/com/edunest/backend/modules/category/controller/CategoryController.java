package com.edunest.backend.modules.category.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.category.dto.CategoryRequest;
import com.edunest.backend.modules.category.dto.CategoryResponse;
import com.edunest.backend.modules.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .success(true).message("Categories fetched successfully")
                .data(service.getActiveCategories()).build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .success(true).message("Category created successfully").data(service.create(request)).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .success(true).message("Category updated successfully").data(service.update(id, request)).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .success(true).message("Category deactivated successfully").data(service.deactivate(id)).build());
    }
}