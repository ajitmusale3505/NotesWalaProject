package com.edunest.backend.modules.assessment.repository;

import com.edunest.backend.modules.assessment.entity.AssessmentComponent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentComponentRepository extends JpaRepository<AssessmentComponent, String> {

    @EntityGraph(attributePaths = {"type", "subjectOffering", "subjectOffering.subject"})
    List<AssessmentComponent> findBySubjectOfferingIdAndActiveTrueOrderByDisplayOrderAsc(
            String subjectOfferingId);
}
