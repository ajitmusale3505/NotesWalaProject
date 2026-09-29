package com.edunest.backend.modules.subject.service;

import com.edunest.backend.modules.subject.dto.SubjectOfferingResponse;

import java.util.List;

public interface SubjectOfferingService {
    List<SubjectOfferingResponse> getByCurriculumSemester(String curriculumSemesterId);
    SubjectOfferingResponse getById(String id);
}
