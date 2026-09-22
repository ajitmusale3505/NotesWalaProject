package com.edunest.backend.modules.branch.repository;

import com.edunest.backend.modules.branch.entity.Branch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    @EntityGraph(attributePaths = {"university", "academicYear"})
    List<Branch> findAllByActiveTrue();
    @EntityGraph(attributePaths = {"university", "academicYear"})
    Optional<Branch> findByIdAndActiveTrue(Long id);
    @EntityGraph(attributePaths = {"university", "academicYear"})
    List<Branch> findByUniversityIdAndActiveTrue(Long universityId);
    @EntityGraph(attributePaths = {"university", "academicYear"})
    List<Branch> findByAcademicYearIdAndActiveTrue(Long academicYearId);
    @EntityGraph(attributePaths = {"university", "academicYear"})
    Optional<Branch> findByCode(String code);
}