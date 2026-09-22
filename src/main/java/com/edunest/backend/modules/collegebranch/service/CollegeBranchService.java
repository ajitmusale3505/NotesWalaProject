package com.edunest.backend.modules.collegebranch.service;

import java.util.List;
import com.edunest.backend.modules.collegebranch.dto.CollegeBranchResponse;

public interface CollegeBranchService {

    List<CollegeBranchResponse> getAll();

    List<CollegeBranchResponse> getByCollegeId(Long collegeId);
}