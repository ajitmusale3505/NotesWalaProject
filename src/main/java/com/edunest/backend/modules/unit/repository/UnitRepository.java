package com.edunest.backend.modules.unit.repository;

import com.edunest.backend.modules.unit.entity.Unit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    @EntityGraph(attributePaths = {"subject"})
    List<Unit> findAllByActiveTrueOrderBySubjectIdAscUnitNumberAsc();

    @EntityGraph(attributePaths = {"subject"})
    Optional<Unit> findByIdAndActiveTrue(Long id);

    @EntityGraph(attributePaths = {"subject"})
    List<Unit> findBySubjectIdAndActiveTrueOrderByUnitNumberAsc(Long subjectId);
    boolean existsBySubjectIdAndUnitNumber(Long subjectId, Integer unitNumber);
    boolean existsBySubjectIdAndUnitNumberAndIdNot(Long subjectId, Integer unitNumber, Long id);
}