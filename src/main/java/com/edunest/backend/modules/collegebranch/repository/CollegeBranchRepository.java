package com.edunest.backend.modules.collegebranch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.collegebranch.entity.CollegeBranch;

@Repository
public interface CollegeBranchRepository
        extends JpaRepository<CollegeBranch, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "college",
            "branch",
            "branch.university",
            "branch.academicYear"
    })
    List<CollegeBranch> findAll();

    @EntityGraph(attributePaths = {
            "college",
            "branch",
            "branch.university",
            "branch.academicYear"
    })
    List<CollegeBranch> findByCollegeId(Long collegeId);

    @EntityGraph(attributePaths = {
            "college",
            "branch",
            "branch.university",
            "branch.academicYear"
    })
    List<CollegeBranch> findByBranchId(Long branchId);

    boolean existsByCollegeIdAndBranchId(Long collegeId, Long branchId);
}