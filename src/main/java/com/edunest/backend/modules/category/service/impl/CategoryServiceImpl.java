package com.edunest.backend.modules.category.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.category.dto.CategoryRequest;
import com.edunest.backend.modules.category.dto.CategoryResponse;
import com.edunest.backend.modules.category.entity.Category;
import com.edunest.backend.modules.category.repository.CategoryRepository;
import com.edunest.backend.modules.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        return repository.findAll().stream()
                .filter(Category::isActive)
                .sorted(Comparator.comparing(Category::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Category::getName))
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String slug = normalizeSlug(request.getSlug());
        if (repository.findBySlug(slug).isPresent()) {
            throw new BadRequestException("Category slug already exists");
        }
        Category category = Category.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(normalize(request.getDescription()))
                .icon(normalize(request.getIcon()))
                .displayOrder(request.getDisplayOrder())
                .active(true)
                .build();
        return map(repository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        String slug = normalizeSlug(request.getSlug());
        repository.findBySlug(slug).filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new BadRequestException("Category slug already exists"); });
        category.setName(request.getName().trim());
        category.setSlug(slug);
        category.setDescription(normalize(request.getDescription()));
        category.setIcon(normalize(request.getIcon()));
        category.setDisplayOrder(request.getDisplayOrder());
        return map(repository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse deactivate(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setActive(false);
        return map(repository.save(category));
    }

    private CategoryResponse map(Category c) {
        return CategoryResponse.builder().id(c.getId()).name(c.getName()).slug(c.getSlug())
                .description(c.getDescription()).icon(c.getIcon()).displayOrder(c.getDisplayOrder())
                .active(c.isActive()).build();
    }

    private String normalizeSlug(String value) {
        String slug = normalize(value);
        if (slug == null) throw new BadRequestException("Category slug is required");
        return slug.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private String normalize(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.isEmpty() ? null : v;
    }
}