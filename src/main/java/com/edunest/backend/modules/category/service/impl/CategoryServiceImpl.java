package com.edunest.backend.modules.category.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.category.dto.CategoryRequest;
import com.edunest.backend.modules.category.dto.CategoryResponse;
import com.edunest.backend.modules.category.entity.Category;
import com.edunest.backend.modules.category.repository.CategoryRepository;
import com.edunest.backend.modules.category.service.CategoryService;
import com.edunest.backend.modules.resource.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final ResourceRepository resourceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        return repository.findAllByActiveTrueOrderByDisplayOrderAscNameAsc()
                .stream()
                .map(category -> map(category, 0L))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        Map<Long, Long> resourceCounts = new HashMap<>();
        for (Object[] row : resourceRepository.findCategoryResourceCounts()) {
            if (row != null && row.length >= 2 && row[0] instanceof Number id && row[1] instanceof Number count) {
                resourceCounts.put(id.longValue(), count.longValue());
            }
        }

        return repository.findAll().stream()
                .sorted(Comparator
                        .comparing(Category::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .map(category -> map(category, resourceCounts.getOrDefault(category.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.getName().trim();
        String slug = normalizeSlug(request.getSlug());
        if (repository.findBySlug(slug).isPresent()) {
            throw new BadRequestException("Category slug already exists");
        }
        Category category = Category.builder()
                .name(name)
                .slug(slug)
                .description(normalize(request.getDescription()))
                .icon(normalize(request.getIcon()))
                .displayOrder(request.getDisplayOrder())
                .active(request.getActive() == null || request.getActive())
                .build();
        return map(repository.save(category), 0L);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getCategory(id);
        String slug = normalizeSlug(request.getSlug());
        repository.findBySlug(slug)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BadRequestException("Category slug already exists");
                });

        category.setName(request.getName().trim());
        category.setSlug(slug);
        category.setDescription(normalize(request.getDescription()));
        category.setIcon(normalize(request.getIcon()));
        category.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) category.setActive(request.getActive());
        return map(repository.save(category), 0L);
    }

    @Override
    @Transactional
    public CategoryResponse updateStatus(Long id, boolean active) {
        Category category = getCategory(id);
        category.setActive(active);
        return map(repository.save(category), 0L);
    }

    @Override
    @Transactional
    public CategoryResponse deactivate(Long id) {
        return updateStatus(id, false);
    }

    private Category getCategory(Long id) {
        if (id == null) throw new BadRequestException("Category is required");
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private CategoryResponse map(Category category, long resourceCount) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .icon(category.getIcon())
                .displayOrder(category.getDisplayOrder())
                .active(category.isActive())
                .resourceCount(resourceCount)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private String normalizeSlug(String value) {
        String slug = normalize(value);
        if (slug == null) throw new BadRequestException("Category slug is required");
        return slug.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    private String normalize(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
