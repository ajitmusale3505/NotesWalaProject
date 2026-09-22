package com.edunest.backend.modules.branch.service.impl;

import com.edunest.backend.common.exception.ResourceNotFoundException;
import com.edunest.backend.common.util.PublicIdUtils;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.edunest.backend.modules.branch.dto.BranchResponseDto;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.branch.service.BranchService;
import com.edunest.backend.modules.collegebranch.entity.CollegeBranch;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CollegeBranchRepository collegeBranchRepository;

    public BranchServiceImpl(
            BranchRepository branchRepository,
            CollegeBranchRepository collegeBranchRepository) {
        this.branchRepository = branchRepository;
        this.collegeBranchRepository = collegeBranchRepository;
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

    @Override
    public List<BranchResponseDto> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BranchResponseDto getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found"));

        return mapToDto(branch);
    }

    @Override
    public List<BranchResponseDto> getBranchesByUniversityId(Long universityId) {
        return branchRepository.findByUniversityId(universityId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchResponseDto> getBranchesByCollegeId(Long collegeId) {
        List<CollegeBranch> mappings =
                collegeBranchRepository.findByCollegeId(collegeId);

        return mappings.stream()
                .map(mapping -> mapToDto(mapping.getBranch()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchResponseDto> getBranchesByAcademicYearId(Long academicYearId) {
        return branchRepository.findByAcademicYearId(academicYearId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
