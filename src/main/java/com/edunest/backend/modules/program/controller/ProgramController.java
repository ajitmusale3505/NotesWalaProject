package com.edunest.backend.modules.program.controller;

import com.edunest.backend.modules.program.dto.ProgramResponse;
import com.edunest.backend.modules.program.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService service;

    @GetMapping
    public List<ProgramResponse> getAll() {
        return service.getAllActive();
    }

    @GetMapping("/{id}")
    public ProgramResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}
