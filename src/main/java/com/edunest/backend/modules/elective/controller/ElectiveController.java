package com.edunest.backend.modules.elective.controller;

import com.edunest.backend.modules.elective.dto.ElectiveGroupResponse;
import com.edunest.backend.modules.elective.service.ElectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/electives")
@RequiredArgsConstructor
public class ElectiveController {

    private final ElectiveService service;

    @GetMapping("/curriculum-semester/{curriculumSemesterId}")
    public List<ElectiveGroupResponse> getByCurriculumSemester(
            @PathVariable String curriculumSemesterId) {
        return service.getByCurriculumSemester(curriculumSemesterId);
    }

    @GetMapping("/{id}")
    public ElectiveGroupResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}
