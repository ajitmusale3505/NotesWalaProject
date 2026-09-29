package com.edunest.backend.modules.resource.repository;

import com.edunest.backend.modules.resource.entity.Resource;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicResourceRepository extends JpaRepository<Resource, Long> {

    @Query("""
        select distinct r
        from Resource r
        join fetch r.subjectOffering so
        join fetch so.subject s
        join fetch so.category c
        join fetch so.curriculumSemester cs
        join fetch cs.curriculum cur
        where r.active = true
          and r.published = true
          and so.active = true
          and cur.active = true
          and cur.university.id = :universityId
          and cur.branch.id = :branchId
          and cur.program.id = :programId
          and cur.examPattern.id = :examPatternId
          and cs.semester.id = :semesterId
          and cs.studyYear = :currentYear
          and (:subjectOfferingId is null or so.id = :subjectOfferingId)
        order by s.name asc, r.title asc
        """)
    List<Resource> findForCurrentAcademicContext(
            Long universityId,
            Long branchId,
            String programId,
            String examPatternId,
            Long semesterId,
            Integer currentYear,
            String subjectOfferingId);
}
