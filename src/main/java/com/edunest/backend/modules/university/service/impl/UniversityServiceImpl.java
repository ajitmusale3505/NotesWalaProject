package com.edunest.backend.modules.university.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.university.dto.response.UniversityResponse;
import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.university.repository.UniversityRepository;
import com.edunest.backend.modules.university.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UniversityServiceImpl implements UniversityService {
    private final UniversityRepository universityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UniversityResponse> getAllUniversities() {
        return universityRepository.findAllByActiveTrue().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UniversityResponse getUniversityById(Long id) {
        University university = universityRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("University not found"));
        return mapToResponse(university);
    }

    private UniversityResponse mapToResponse(University university) {
        return UniversityResponse.builder()
                .id(PublicIdUtils.universityId(university.getId()))
                .name(university.getName())
                .shortCode(university.getShortCode())
                .city(university.getCity())
                .state(university.getState())
                .country(university.getCountry())
                .active(university.isActive())
                .build();
    }
}