package com.edunest.backend.modules.college.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.college.dto.response.CollegeResponse;
import com.edunest.backend.modules.college.service.CollegeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/colleges")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;

    @GetMapping
    public List<CollegeResponse> getAllColleges() {
        return collegeService.getAllColleges();
    }

    @GetMapping("/{id}")
    public CollegeResponse getCollegeById(
            @PathVariable Long id) {
        return collegeService.getCollegeById(id);
    }

    @GetMapping("/university/{universityId}")
    public List<CollegeResponse> getCollegesByUniversityId(
            @PathVariable Long universityId) {
        return collegeService.getCollegesByUniversityId(universityId);
    }
}