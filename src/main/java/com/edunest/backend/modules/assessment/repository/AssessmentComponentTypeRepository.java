package com.edunest.backend.modules.assessment.repository;

import com.edunest.backend.modules.assessment.entity.AssessmentComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentComponentTypeRepository extends JpaRepository<AssessmentComponentType, String> {
    List<AssessmentComponentType> findByActiveTrueOrderByNameAsc();
    Optional<AssessmentComponentType> findByCodeIgnoreCase(String code);
}
