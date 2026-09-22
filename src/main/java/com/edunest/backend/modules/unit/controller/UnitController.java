package com.edunest.backend.modules.unit.controller;

import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.unit.dto.UnitResponse;
import com.edunest.backend.modules.unit.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    public List<UnitResponse> getAllUnits() {
        return unitService.getAllUnits();
    }

    @GetMapping("/{id}")
    public UnitResponse getUnitById(@PathVariable String id) {
        return unitService.getUnitById(PublicIdUtils.parseUnitId(id));
    }

    @GetMapping("/subject/{subjectId}")
    public List<UnitResponse> getUnitsBySubject(@PathVariable String subjectId) {
        return unitService.getUnitsBySubjectId(PublicIdUtils.parseSubjectId(subjectId));
    }
}