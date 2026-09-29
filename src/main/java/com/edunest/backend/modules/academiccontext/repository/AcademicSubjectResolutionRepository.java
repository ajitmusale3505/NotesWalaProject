package com.edunest.backend.modules.academiccontext.repository;

import com.edunest.backend.modules.subject.entity.SubjectOffering;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AcademicSubjectResolutionRepository extends JpaRepository<SubjectOffering, String> {

    @Query("""
        select distinct so
        from SubjectOffering so
        join fetch so.subject s
        join fetch so.category c
        join fetch so.curriculumSemester cs
        join fetch cs.curriculum cur
        join fetch cs.semester sem
        where so.active = true
          and cur.active = true
          and cs.active = true
          and cur.university.id = :universityId
          and cur.branch.id = :branchId
          and cur.program.id = :programId
          and cur.examPattern.id = :examPatternId
          and cs.semester.id = :semesterId
          and cs.studyYear = :currentYear
        order by so.code asc
        """)
    List<SubjectOffering> findSubjectsForContext(
            Long universityId, Long branchId, String programId,
            String examPatternId, Long semesterId, Integer currentYear);
}
