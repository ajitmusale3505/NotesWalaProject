package com.edunest.backend.modules.unit.service;

import com.edunest.backend.modules.unit.dto.UnitResponse;
import java.util.List;

public interface UnitService {
    List<UnitResponse> getAllUnits();
    UnitResponse getUnitById(Long id);
    List<UnitResponse> getUnitsBySubjectId(Long subjectId);
}