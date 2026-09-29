package com.edunest.backend.modules.curriculum.repository;

import com.edunest.backend.modules.curriculum.entity.CurriculumSemester;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumSemesterRepository extends JpaRepository<CurriculumSemester, String> {

    @EntityGraph(attributePaths = {"curriculum", "semester"})
    List<CurriculumSemester> findByCurriculumIdAndActiveTrueOrderByStudyYearAscDisplayOrderAsc(
            String curriculumId);

    @EntityGraph(attributePaths = {"curriculum", "semester"})
    Optional<CurriculumSemester> findByIdAndActiveTrue(String id);

    boolean existsByCurriculumIdAndSemesterId(String curriculumId, Long semesterId);
}
