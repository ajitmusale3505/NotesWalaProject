package com.edunest.backend.modules.exampattern.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.modules.exampattern.dto.ExamPatternResponse;
import com.edunest.backend.modules.exampattern.entity.ExamPattern;
import com.edunest.backend.modules.exampattern.repository.ExamPatternRepository;
import com.edunest.backend.modules.exampattern.service.ExamPatternService;
import com.edunest.backend.common.util.PublicIdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamPatternServiceImpl implements ExamPatternService {

    private final ExamPatternRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ExamPatternResponse> getAllActive() {
        return repository.findAllByActiveTrueOrderByEffectiveFromYearDescNameAsc().stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamPatternResponse getById(String id) {
        return repository.findByIdAndActiveTrue(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Exam pattern not found"));
    }

    private ExamPatternResponse map(ExamPattern pattern) {
        return ExamPatternResponse.builder()
                .id(pattern.getId())
                .name(pattern.getName())
                .code(pattern.getCode())
                .description(pattern.getDescription())
                .effectiveFromYear(pattern.getEffectiveFromYear())
                .effectiveToYear(pattern.getEffectiveToYear())
                .universityId(PublicIdUtils.universityId(pattern.getUniversity().getId()))
                .universityName(pattern.getUniversity().getName())
                .active(pattern.isActive())
                .build();
    }
}
