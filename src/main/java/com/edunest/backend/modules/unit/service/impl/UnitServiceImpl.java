package com.edunest.backend.modules.unit.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.unit.dto.UnitResponse;
import com.edunest.backend.modules.unit.entity.Unit;
import com.edunest.backend.modules.unit.repository.UnitRepository;
import com.edunest.backend.modules.unit.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UnitResponse> getAllUnits() {
        return unitRepository.findAllByActiveTrueOrderBySubjectIdAscUnitNumberAsc()
                .stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnitResponse getUnitById(Long id) {
        return unitRepository.findByIdAndActiveTrue(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitResponse> getUnitsBySubjectId(Long subjectId) {
        return unitRepository.findBySubjectIdAndActiveTrueOrderByUnitNumberAsc(subjectId)
                .stream().map(this::map).toList();
    }

    private UnitResponse map(Unit unit) {
        return UnitResponse.builder()
                .id(PublicIdUtils.unitId(unit.getId()))
                .unitNumber(unit.getUnitNumber())
                .chapterName(unit.getChapterName())
                .description(unit.getDescription())
                .active(unit.isActive())
                .subjectId(PublicIdUtils.subjectId(unit.getSubject().getId()))
                .subjectName(unit.getSubject().getName())
                .subjectCode(unit.getSubject().getCode())
                .build();
    }
}