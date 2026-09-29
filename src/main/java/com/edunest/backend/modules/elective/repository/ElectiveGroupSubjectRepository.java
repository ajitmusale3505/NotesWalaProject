package com.edunest.backend.modules.elective.repository;

import com.edunest.backend.modules.elective.entity.ElectiveGroupSubject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectiveGroupSubjectRepository extends JpaRepository<ElectiveGroupSubject, String> {

    @EntityGraph(attributePaths = {"subjectOffering", "subjectOffering.subject",
            "subjectOffering.category"})
    List<ElectiveGroupSubject> findByElectiveGroupIdAndActiveTrueOrderByDisplayOrderAsc(
            String electiveGroupId);

    long countByElectiveGroupIdAndActiveTrue(String electiveGroupId);
}
