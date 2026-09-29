package com.edunest.backend.modules.elective.service;

import com.edunest.backend.modules.elective.dto.ElectiveGroupResponse;

import java.util.List;

public interface ElectiveService {
    List<ElectiveGroupResponse> getByCurriculumSemester(String curriculumSemesterId);
    ElectiveGroupResponse getById(String id);
}
