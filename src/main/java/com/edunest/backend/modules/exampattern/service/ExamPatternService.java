package com.edunest.backend.modules.exampattern.service;

import com.edunest.backend.modules.exampattern.dto.ExamPatternResponse;

import java.util.List;

public interface ExamPatternService {
    List<ExamPatternResponse> getAllActive();
    ExamPatternResponse getById(String id);
}
