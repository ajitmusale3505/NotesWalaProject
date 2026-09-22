package com.edunest.backend.modules.university.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.university.dto.response.UniversityResponse;
import com.edunest.backend.modules.university.service.UniversityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @GetMapping
    public List<UniversityResponse> getAllUniversities() {
        return universityService.getAllUniversities();
    }

    @GetMapping("/{id}")
    public UniversityResponse getUniversityById(
            @PathVariable String id) {
        return universityService.getUniversityById(
                PublicIdUtils.parseUniversityId(id));
    }
}
