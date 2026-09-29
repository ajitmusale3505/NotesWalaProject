package com.edunest.backend.modules.subject.controller;

import com.edunest.backend.modules.subject.dto.SubjectCategoryResponse;
import com.edunest.backend.modules.subject.repository.SubjectCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject-categories")
@RequiredArgsConstructor
public class SubjectCategoryController {

    private final SubjectCategoryRepository repository;

    @GetMapping
    public List<SubjectCategoryResponse> getActive() {
        return repository.findByActiveTrueOrderByNameAsc().stream()
                .map(c -> SubjectCategoryResponse.builder()
                        .id(c.getId())
                        .code(c.getCode())
                        .name(c.getName())
                        .description(c.getDescription())
                        .active(c.isActive())
                        .build())
                .toList();
    }
}
