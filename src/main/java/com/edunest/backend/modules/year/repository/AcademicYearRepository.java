package com.edunest.backend.modules.year.repository;

import com.edunest.backend.modules.year.entity.AcademicYear;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    @EntityGraph(attributePaths = {"university"})
    List<AcademicYear> findAllByActiveTrue();
    @EntityGraph(attributePaths = {"university"})
    Optional<AcademicYear> findByIdAndActiveTrue(Long id);
    @EntityGraph(attributePaths = {"university"})
    List<AcademicYear> findByUniversityIdAndActiveTrue(Long universityId);
    Optional<AcademicYear> findByCode(String code);
}