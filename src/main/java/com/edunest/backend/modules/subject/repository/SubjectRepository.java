package com.edunest.backend.modules.subject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.subject.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @EntityGraph(attributePaths = {"branch", "semester", "academicYear"})
    List<Subject> findByActiveTrue();

    @Override
    @EntityGraph(attributePaths = {"branch", "semester", "academicYear"})
    Optional<Subject> findById(Long id);

    @EntityGraph(attributePaths = {"branch", "semester", "academicYear"})
    List<Subject> findByBranchIdAndActiveTrue(Long branchId);

    @EntityGraph(attributePaths = {"branch", "semester", "academicYear"})
    List<Subject> findBySemesterIdAndActiveTrue(Long semesterId);

    @EntityGraph(attributePaths = {"branch", "semester", "academicYear"})
    List<Subject> findByBranchIdAndSemesterIdAndActiveTrue(
            Long branchId,
            Long semesterId
    );
}