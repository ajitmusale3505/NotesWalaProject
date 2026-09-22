package com.edunest.backend.modules.university.service;

import java.util.List;

import com.edunest.backend.modules.university.dto.response.UniversityResponse;

public interface UniversityService {

    List<UniversityResponse> getAllUniversities();

    UniversityResponse getUniversityById(Long id);
}