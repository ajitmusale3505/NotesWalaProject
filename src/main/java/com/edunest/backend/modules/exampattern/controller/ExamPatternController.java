package com.edunest.backend.modules.exampattern.controller;

import com.edunest.backend.modules.exampattern.dto.ExamPatternResponse;
import com.edunest.backend.modules.exampattern.service.ExamPatternService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam-patterns")
@RequiredArgsConstructor
public class ExamPatternController {

    private final ExamPatternService service;

    @GetMapping
    public List<ExamPatternResponse> getAll() {
        return service.getAllActive();
    }

    @GetMapping("/{id}")
    public ExamPatternResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}
