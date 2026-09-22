package com.edunest.backend.modules.branch.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.branch.dto.BranchResponseDto;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.branch.service.BranchService;
import com.edunest.backend.modules.collegebranch.entity.CollegeBranch;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final CollegeBranchRepository collegeBranchRepository;

    public BranchServiceImpl(BranchRepository branchRepository, CollegeBranchRepository collegeBranchRepository) {
        this.branchRepository = branchRepository;
        this.collegeBranchRepository = collegeBranchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponseDto> getAllBranches() {
        return branchRepository.findAllByActiveTrue().stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponseDto getBranchById(Long id) {
        return branchRepository.findByIdAndActiveTrue(id).map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponseDto> getBranchesByUniversityId(Long universityId) {
        return branchRepository.findByUniversityIdAndActiveTrue(universityId).stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponseDto> getBranchesByCollegeId(Long collegeId) {
        return collegeBranchRepository.findByCollegeIdAndActiveTrue(collegeId)
                .stream().map(mapping -> mapToDto(mapping.getBranch())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponseDto> getBranchesByAcademicYearId(Long academicYearId) {
        return branchRepository.findByAcademicYearIdAndActiveTrue(academicYearId).stream().map(this::mapToDto).toList();
    }

    private BranchResponseDto mapToDto(Branch branch) {
        return BranchResponseDto.builder()
                .id(PublicIdUtils.branchId(branch.getId()))
                .name(branch.getName())
                .code(branch.getCode())
                .active(branch.isActive())
                .universityId(PublicIdUtils.universityId(branch.getUniversity().getId()))
                .universityName(branch.getUniversity().getName())
                .academicYearId(PublicIdUtils.academicYearId(branch.getAcademicYear().getId()))
                .academicYearName(branch.getAcademicYear().getName())
                .build();
    }
}