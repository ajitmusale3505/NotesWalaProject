package com.edunest.backend.modules.collegebranch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.edunest.backend.modules.collegebranch.dto.CollegeBranchResponse;
import com.edunest.backend.modules.collegebranch.service.CollegeBranchService;

@RestController
@RequestMapping("/college-branches")
public class CollegeBranchController {

    private final CollegeBranchService service;

    public CollegeBranchController(CollegeBranchService service) {
        this.service = service;
    }

    @GetMapping
    public List<CollegeBranchResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/college/{collegeId}")
    public List<CollegeBranchResponse> getByCollege(
            @PathVariable Long collegeId) {
        return service.getByCollegeId(collegeId);
    }
}