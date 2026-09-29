package com.edunest.backend.modules.elective.repository;

import com.edunest.backend.modules.elective.entity.ElectiveGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectiveGroupRepository extends JpaRepository<ElectiveGroup, String> {

    @EntityGraph(attributePaths = {"curriculumSemester", "curriculumSemester.curriculum",
            "curriculumSemester.semester"})
    List<ElectiveGroup> findByCurriculumSemesterIdAndActiveTrueOrderByDisplayOrderAsc(
            String curriculumSemesterId);

    @EntityGraph(attributePaths = {"curriculumSemester", "curriculumSemester.curriculum",
            "curriculumSemester.semester"})
    Optional<ElectiveGroup> findByIdAndActiveTrue(String id);
}
