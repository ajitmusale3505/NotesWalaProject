package com.edunest.backend.modules.curriculum.service;

import com.edunest.backend.modules.curriculum.dto.CurriculumResponse;
import com.edunest.backend.modules.curriculum.dto.CurriculumSemesterResponse;

import java.util.List;

public interface CurriculumService {
    List<CurriculumResponse> getAllActive();
    CurriculumResponse getById(String id);
    List<CurriculumResponse> getByBranchId(String branchId);
    List<CurriculumSemesterResponse> getSemesters(String curriculumId);
}
