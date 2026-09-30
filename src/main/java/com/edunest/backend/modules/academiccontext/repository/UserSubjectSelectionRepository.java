package com.edunest.backend.modules.academiccontext.repository;

import com.edunest.backend.modules.academiccontext.entity.UserSubjectSelection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubjectSelectionRepository extends JpaRepository<UserSubjectSelection, String> {

    @EntityGraph(attributePaths = {
            "subjectOffering",
            "subjectOffering.subject",
            "subjectOffering.category",
            "subjectOffering.curriculumSemester",
            "subjectOffering.curriculumSemester.curriculum",
            "subjectOffering.curriculumSemester.semester"
    })
    List<UserSubjectSelection> findByUserIdOrderByCreatedAtAsc(Long userId);

    Optional<UserSubjectSelection> findByUserIdAndSubjectOfferingId(Long userId, String subjectOfferingId);
}
