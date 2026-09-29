package com.edunest.backend.modules.program.service;

import com.edunest.backend.modules.program.dto.ProgramResponse;

import java.util.List;

public interface ProgramService {
    List<ProgramResponse> getAllActive();
    ProgramResponse getById(String id);
}
