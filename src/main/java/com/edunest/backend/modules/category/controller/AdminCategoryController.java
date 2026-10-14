package com.edunest.backend.modules.category.controller;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.modules.category.dto.CategoryResponse;
import com.edunest.backend.modules.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Administrator-only read endpoint for the category management workspace.
 * Kept under /admin to avoid collisions with /categories/{id} mutation routes.
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .success(true)
                .message("Category management data fetched successfully")
                .data(categoryService.getAllCategories())
                .build());
    }
}
