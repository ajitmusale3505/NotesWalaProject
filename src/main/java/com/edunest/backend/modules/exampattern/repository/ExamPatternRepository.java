package com.edunest.backend.modules.exampattern.repository;

import com.edunest.backend.modules.exampattern.entity.ExamPattern;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamPatternRepository extends JpaRepository<ExamPattern, String> {

    @EntityGraph(attributePaths = "university")
    List<ExamPattern> findAllByActiveTrueOrderByEffectiveFromYearDescNameAsc();

    @EntityGraph(attributePaths = "university")
    Optional<ExamPattern> findByIdAndActiveTrue(String id);

    boolean existsByUniversityIdAndCodeIgnoreCase(Long universityId, String code);
}
