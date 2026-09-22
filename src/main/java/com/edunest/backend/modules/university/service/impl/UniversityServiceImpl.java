package com.edunest.backend.modules.university.service.impl;

import com.edunest.backend.common.exception.BadRequestException;
import com.edunest.backend.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.university.dto.response.UniversityResponse;
import com.edunest.backend.modules.university.entity.University;
import com.edunest.backend.modules.university.repository.UniversityRepository;
import com.edunest.backend.modules.university.service.UniversityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UniversityServiceImpl implements UniversityService {

    private final UniversityRepository universityRepository;

    @Override
    public List<UniversityResponse> getAllUniversities() {
        return universityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UniversityResponse getUniversityById(Long id) {

        University university = universityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("University not found"));

        return mapToResponse(university);
    }

    private UniversityResponse mapToResponse(
            University university) {

        return UniversityResponse.builder()
                .id(university.getId())
                .name(university.getName())
                .shortCode(university.getShortCode())
                .active(university.isActive())
                .build();
    }
}