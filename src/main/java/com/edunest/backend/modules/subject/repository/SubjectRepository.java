package com.edunest.backend.modules.subject.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edunest.backend.modules.subject.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByActiveTrue();

    List<Subject> findByBranchIdAndActiveTrue(Long branchId);

    List<Subject> findBySemesterIdAndActiveTrue(Long semesterId);

    List<Subject> findByBranchIdAndSemesterIdAndActiveTrue(
            Long branchId,
            Long semesterId
    );
    
}