package com.edunest.backend.modules.category.service;

import com.edunest.backend.modules.category.dto.CategoryRequest;
import com.edunest.backend.modules.category.dto.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getActiveCategories();
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    CategoryResponse deactivate(Long id);
}