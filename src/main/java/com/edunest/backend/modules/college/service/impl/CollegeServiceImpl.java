package com.edunest.backend.modules.college.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.college.dto.response.CollegeResponse;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.college.repository.CollegeRepository;
import com.edunest.backend.modules.college.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeServiceImpl implements CollegeService {
    private final CollegeRepository collegeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CollegeResponse> getAllColleges() {
        return collegeRepository.findAllByActiveTrue().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeResponse getCollegeById(Long id) {
        College college = collegeRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        return mapToResponse(college);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeResponse> getCollegesByUniversityId(Long universityId) {
        return collegeRepository.findByUniversityIdAndActiveTrue(universityId)
                .stream().map(this::mapToResponse).toList();
    }

    private CollegeResponse mapToResponse(College college) {
        return CollegeResponse.builder()
                .id(PublicIdUtils.collegeId(college.getId()))
                .name(college.getName())
                .code(college.getCode())
                .city(college.getCity())
                .state(college.getState())
                .active(college.isActive())
                .universityId(PublicIdUtils.universityId(college.getUniversity().getId()))
                .universityName(college.getUniversity().getName())
                .build();
    }
}