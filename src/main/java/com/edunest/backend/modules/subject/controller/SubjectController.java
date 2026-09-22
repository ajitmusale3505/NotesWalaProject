package com.edunest.backend.modules.subject.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edunest.backend.common.response.ApiResponse;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.subject.dto.SubjectResponseDto;
import com.edunest.backend.modules.subject.service.SubjectService;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getAllSubjects() {

        ApiResponse<List<SubjectResponseDto>> response =
                ApiResponse.<List<SubjectResponseDto>>builder()
                        .success(true)
                        .message("Subjects fetched successfully")
                        .data(subjectService.getAllSubjects())
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDto>> getSubjectById(
            @PathVariable String id) {

        ApiResponse<SubjectResponseDto> response =
                ApiResponse.<SubjectResponseDto>builder()
                        .success(true)
                        .message("Subject fetched successfully")
                        .data(subjectService.getSubjectById(
                                PublicIdUtils.parseSubjectId(id)))
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getSubjectsByBranch(
            @PathVariable String branchId) {

        ApiResponse<List<SubjectResponseDto>> response =
                ApiResponse.<List<SubjectResponseDto>>builder()
                        .success(true)
                        .message("Subjects fetched successfully")
                        .data(subjectService.getSubjectsByBranch(
                                PublicIdUtils.parseBranchId(branchId)))
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getSubjectsBySemester(
            @PathVariable String semesterId) {

        ApiResponse<List<SubjectResponseDto>> response =
                ApiResponse.<List<SubjectResponseDto>>builder()
                        .success(true)
                        .message("Subjects fetched successfully")
                        .data(subjectService.getSubjectsBySemester(
                                PublicIdUtils.parseSemesterId(semesterId)))
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> filterSubjects(
            @RequestParam String branchId,
            @RequestParam String semesterId) {

        ApiResponse<List<SubjectResponseDto>> response =
                ApiResponse.<List<SubjectResponseDto>>builder()
                        .success(true)
                        .message("Filtered subjects fetched successfully")
                        .data(subjectService.getSubjectsByBranchAndSemester(
                                PublicIdUtils.parseBranchId(branchId),
                                PublicIdUtils.parseSemesterId(semesterId)))
                        .build();

        return ResponseEntity.ok(response);
    }
}
