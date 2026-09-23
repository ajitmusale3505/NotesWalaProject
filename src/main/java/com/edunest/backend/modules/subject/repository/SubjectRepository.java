package com.edunest.backend.modules.subject.repository;

import com.edunest.backend.modules.subject.entity.Subject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @EntityGraph(attributePaths = {"branch", "branch.university", "branch.academicYear", "semester", "semester.academicYear", "academicYear", "academicYear.university"})
    List<Subject> findByActiveTrue();

    @Override
    @EntityGraph(attributePaths = {"branch", "branch.university", "branch.academicYear", "semester", "semester.academicYear", "academicYear", "academicYear.university"})
    Optional<Subject> findById(Long id);

    @EntityGraph(attributePaths = {"branch", "branch.university", "branch.academicYear", "semester", "semester.academicYear", "academicYear", "academicYear.university"})
    List<Subject> findByBranchIdAndActiveTrue(Long branchId);

    @EntityGraph(attributePaths = {"branch", "branch.university", "branch.academicYear", "semester", "semester.academicYear", "academicYear", "academicYear.university"})
    List<Subject> findBySemesterIdAndActiveTrue(Long semesterId);

    @EntityGraph(attributePaths = {"branch", "branch.university", "branch.academicYear", "semester", "semester.academicYear", "academicYear", "academicYear.university"})
    List<Subject> findByBranchIdAndSemesterIdAndActiveTrue(Long branchId, Long semesterId);
    Optional<Subject> findByCode(String code);
}