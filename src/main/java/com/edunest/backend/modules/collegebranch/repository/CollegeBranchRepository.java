package com.edunest.backend.modules.collegebranch.repository;

import com.edunest.backend.modules.collegebranch.entity.CollegeBranch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CollegeBranchRepository extends JpaRepository<CollegeBranch, Long> {

    @EntityGraph(attributePaths = {"college", "branch", "branch.university", "branch.academicYear"})
    List<CollegeBranch> findAllByActiveTrue();

    @EntityGraph(attributePaths = {"college", "branch", "branch.university", "branch.academicYear"})
    List<CollegeBranch> findByCollegeIdAndActiveTrue(Long collegeId);

    @EntityGraph(attributePaths = {"college", "branch", "branch.university", "branch.academicYear"})
    List<CollegeBranch> findByBranchIdAndActiveTrue(Long branchId);

    boolean existsByCollegeIdAndBranchId(Long collegeId, Long branchId);

    boolean existsByCollegeIdAndBranchIdAndActiveTrue(Long collegeId, Long branchId);
}