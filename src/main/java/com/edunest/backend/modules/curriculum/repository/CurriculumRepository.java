package com.edunest.backend.modules.curriculum.repository;

import com.edunest.backend.modules.curriculum.entity.Curriculum;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, String> {

    @EntityGraph(attributePaths = {"university", "program", "branch", "examPattern"})
    List<Curriculum> findAllByActiveTrueOrderByStartYearDescNameAsc();

    @EntityGraph(attributePaths = {"university", "program", "branch", "examPattern"})
    Optional<Curriculum> findByIdAndActiveTrue(String id);

    @EntityGraph(attributePaths = {"university", "program", "branch", "examPattern"})
    List<Curriculum> findByBranchIdAndActiveTrueOrderByStartYearDesc(Long branchId);

    @EntityGraph(attributePaths = {"university", "program", "branch", "examPattern"})
    List<Curriculum> findByExamPatternIdAndActiveTrueOrderByStartYearDesc(String examPatternId);

    boolean existsByBranchIdAndExamPatternIdAndCodeIgnoreCase(
            Long branchId, String examPatternId, String code);
}
