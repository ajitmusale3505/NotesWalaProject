package com.edunest.backend.modules.branch.service.impl;

import com.edunest.backend.common.util.PublicIdUtils;
import com.edunest.backend.modules.branch.dto.BranchResponseDto;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.branch.repository.BranchRepository;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.collegebranch.entity.CollegeBranch;
import com.edunest.backend.modules.collegebranch.repository.CollegeBranchRepository;
import com.edunest.backend.modules.university.entity.University;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private CollegeBranchRepository collegeBranchRepository;

    @InjectMocks
    private BranchServiceImpl branchService;

    @Test
    void getBranchesByCollegeHandlesBranchWithoutAcademicYear() {
        University university = University.builder()
                .id(1L)
                .name("Example University")
                .shortCode("EXU")
                .active(true)
                .build();

        College college = College.builder()
                .id(1L)
                .name("Example College")
                .code("EXC")
                .active(true)
                .university(university)
                .build();

        Branch branch = Branch.builder()
                .id(1L)
                .name("Computer Engineering")
                .code("COMP")
                .active(true)
                .university(university)
                .academicYear(null)
                .build();

        CollegeBranch mapping = CollegeBranch.builder()
                .id(1L)
                .college(college)
                .branch(branch)
                .active(true)
                .build();

        when(collegeBranchRepository.findByCollegeIdAndActiveTrue(1L))
                .thenReturn(List.of(mapping));

        List<BranchResponseDto> result = branchService.getBranchesByCollegeId(1L);

        assertEquals(1, result.size());
        assertEquals(PublicIdUtils.branchId(1L), result.get(0).getId());
        assertEquals("Computer Engineering", result.get(0).getName());
        assertEquals(PublicIdUtils.universityId(1L), result.get(0).getUniversityId());
        assertNull(result.get(0).getAcademicYearId());
        assertNull(result.get(0).getAcademicYearName());
    }
}
