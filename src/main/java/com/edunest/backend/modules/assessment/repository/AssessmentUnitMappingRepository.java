package com.edunest.backend.modules.assessment.repository;

import com.edunest.backend.modules.assessment.entity.AssessmentUnitMapping;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssessmentUnitMappingRepository extends JpaRepository<AssessmentUnitMapping, String> {
    @EntityGraph(attributePaths = {"assessmentComponent", "assessmentComponent.type", "unit", "unit.subject"})
    List<AssessmentUnitMapping> findByAssessmentComponentIdAndActiveTrueOrderByDisplayOrderAsc(String assessmentComponentId);
    @EntityGraph(attributePaths = {"assessmentComponent", "assessmentComponent.type", "unit", "unit.subject"})
    List<AssessmentUnitMapping> findByUnitIdAndActiveTrueOrderByDisplayOrderAsc(Long unitId);
    boolean existsByAssessmentComponentIdAndUnitId(String assessmentComponentId, Long unitId);
}
