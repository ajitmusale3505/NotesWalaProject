package com.edunest.backend.modules.subject.repository;

import com.edunest.backend.modules.subject.entity.SubjectOffering;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectOfferingRepository extends JpaRepository<SubjectOffering, String> {

    @EntityGraph(attributePaths = {"subject", "curriculumSemester", "curriculumSemester.curriculum",
            "curriculumSemester.semester"})
    List<SubjectOffering> findByCurriculumSemesterIdAndActiveTrueOrderByCodeAsc(
            String curriculumSemesterId);

    @EntityGraph(attributePaths = {"subject", "curriculumSemester", "curriculumSemester.curriculum",
            "curriculumSemester.semester"})
    Optional<SubjectOffering> findByIdAndActiveTrue(String id);

    boolean existsByCurriculumSemesterIdAndSubjectId(
            String curriculumSemesterId, Long subjectId);
}
