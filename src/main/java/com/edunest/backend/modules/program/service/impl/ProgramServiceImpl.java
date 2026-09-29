package com.edunest.backend.modules.program.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.id.BusinessIdGenerator;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.program.dto.ProgramResponse;
import com.edunest.backend.modules.program.entity.Program;
import com.edunest.backend.modules.program.repository.ProgramRepository;
import com.edunest.backend.modules.program.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> getAllActive() {
        return repository.findAllByActiveTrueOrderByNameAsc().stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramResponse getById(String id) {
        return repository.findByIdAndActiveTrue(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));
    }

    private ProgramResponse map(Program program) {
        return ProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .code(program.getCode())
                .degreeLevel(program.getDegreeLevel())
                .universityId(PublicIdUtils.universityId(program.getUniversity().getId()))
                .universityName(program.getUniversity().getName())
                .active(program.isActive())
                .build();
    }
}
