package com.edunest.backend.modules.collegebranch.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.collegebranch.dto.CollegeBranchResponse;
import com.edunest.backend.modules.collegebranch.entity.CollegeBranch;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import com.edunest.backend.modules.collegebranch.service.CollegeBranchService;

@Service
public class CollegeBranchServiceImpl implements CollegeBranchService {

    private final CollegeBranchRepository repository;

    public CollegeBranchServiceImpl(CollegeBranchRepository repository) {
        this.repository = repository;
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

    @Override
    public List<CollegeBranchResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CollegeBranchResponse> getByCollegeId(Long collegeId) {
        return repository.findByCollegeId(collegeId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }
}
