package com.edunest.backend.modules.branch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.branch.entity.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByUniversityId(Long universityId);

    List<Branch> findByAcademicYearId(Long academicYearId);

    Optional<Branch> findByCode(String code);
}