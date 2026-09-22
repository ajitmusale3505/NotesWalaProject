package com.edunest.backend.modules.college.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.college.dto.response.CollegeResponse;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.college.service.CollegeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;

    @Override
    public List<CollegeResponse> getAllColleges() {
        return collegeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CollegeResponse getCollegeById(Long id) {

        College college = collegeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("College not found"));

        return mapToResponse(college);
    }

    @Override
    public List<CollegeResponse> getCollegesByUniversityId(Long universityId) {

        return collegeRepository.findByUniversityId(universityId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CollegeResponse mapToResponse(College college) {

        return CollegeResponse.builder()
                .id(PublicIdUtils.collegeId(college.getId()))
                .name(college.getName())
                .code(college.getCode())
                .active(college.isActive())
                .universityId(PublicIdUtils.universityId(college.getUniversity().getId()))
                .universityName(college.getUniversity().getName())
                .build();
    }
}
