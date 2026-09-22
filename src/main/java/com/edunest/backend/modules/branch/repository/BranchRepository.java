package com.edunest.backend.modules.branch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.branch.entity.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Override
    @EntityGraph(attributePaths = {"university", "academicYear"})
    List<Branch> findAll();

    @Override
    @EntityGraph(attributePaths = {"university", "academicYear"})
    Optional<Branch> findById(Long id);

    @EntityGraph(attributePaths = {"university", "academicYear"})
    List<Branch> findByUniversityId(Long universityId);

    @EntityGraph(attributePaths = {"university", "academicYear"})
    List<Branch> findByAcademicYearId(Long academicYearId);

    @EntityGraph(attributePaths = {"university", "academicYear"})
    Optional<Branch> findByCode(String code);
}