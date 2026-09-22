package com.edunest.backend.modules.collegebranch.service.impl;

import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.collegebranch.dto.CollegeBranchResponse;
import com.edunest.backend.modules.collegebranch.entity.CollegeBranch;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import com.edunest.backend.modules.collegebranch.service.CollegeBranchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CollegeBranchServiceImpl implements CollegeBranchService {
    private final CollegeBranchRepository repository;
    public CollegeBranchServiceImpl(CollegeBranchRepository repository) { this.repository = repository; }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeBranchResponse> getAll() {
        return repository.findAllByActiveTrue().stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeBranchResponse> getByCollegeId(Long collegeId) {
        return repository.findByCollegeIdAndActiveTrue(collegeId).stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeBranchResponse> getByBranchId(Long branchId) {
        return repository.findByBranchIdAndActiveTrue(branchId).stream().map(this::map).toList();
    }

    private CollegeBranchResponse map(CollegeBranch cb) {
        return CollegeBranchResponse.builder()
                .id(PublicIdUtils.collegeBranchId(cb.getId()))
                .collegeId(PublicIdUtils.collegeId(cb.getCollege().getId()))
                .collegeName(cb.getCollege().getName())
                .branchId(PublicIdUtils.branchId(cb.getBranch().getId()))
                .branchName(cb.getBranch().getName())
                .branchCode(cb.getBranch().getCode())
                .active(cb.isActive())
                .build();
    }
}