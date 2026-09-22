package com.edunest.backend.modules.college.service;

import java.util.List;

import com.edunest.backend.modules.college.dto.response.CollegeResponse;

public interface CollegeService {

    List<CollegeResponse> getAllColleges();

    CollegeResponse getCollegeById(Long id);

    List<CollegeResponse> getCollegesByUniversityId(Long universityId);
}