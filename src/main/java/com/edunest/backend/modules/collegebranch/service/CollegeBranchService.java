package com.edunest.backend.modules.collegebranch.service;

import com.edunest.backend.modules.collegebranch.dto.CollegeBranchResponse;
import java.util.List;

public interface CollegeBranchService {
    List<CollegeBranchResponse> getAll();
    List<CollegeBranchResponse> getByCollegeId(Long collegeId);
    List<CollegeBranchResponse> getByBranchId(Long branchId);
}