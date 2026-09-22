package com.edunest.backend.modules.semester.repository;

import com.edunest.backend.modules.semester.entity.Semester;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    @EntityGraph(attributePaths = {"academicYear"})
    List<Semester> findAllByActiveTrue();
    @EntityGraph(attributePaths = {"academicYear"})
    Optional<Semester> findByIdAndActiveTrue(Long id);
    @EntityGraph(attributePaths = {"academicYear"})
    List<Semester> findByAcademicYearIdAndActiveTrue(Long academicYearId);
    @EntityGraph(attributePaths = {"academicYear"})
    Optional<Semester> findByNumberAndAcademicYear_Code(Integer number, String code);
}