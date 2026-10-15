package com.edunest.backend.modules.userprofile.repository;

import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAcademicProfileRepository extends JpaRepository<UserAcademicProfile, Long> {

    @EntityGraph(attributePaths = {
            "user", "university", "college", "branch", "program", "examPattern", "academicYear", "currentSemester"
    })
    Optional<UserAcademicProfile> findByUserIdAndActiveTrue(Long userId);

    @EntityGraph(attributePaths = {
            "user", "university", "college", "branch", "academicYear", "currentSemester"
    })
    Optional<UserAcademicProfile> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "branch", "currentSemester"})
    Page<UserAcademicProfile> findByUniversity_IdAndActiveTrue(Long universityId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "branch", "currentSemester"})
    Page<UserAcademicProfile> findByUniversity_IdAndBranch_IdAndActiveTrue(
            Long universityId, Long branchId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "branch", "currentSemester"})
    Page<UserAcademicProfile> findByCollege_IdAndBranch_IdAndActiveTrue(
            Long collegeId, Long branchId, Pageable pageable);

    long countByUniversity_IdAndActiveTrue(Long universityId);

    long countByUniversity_IdAndBranch_IdAndActiveTrue(Long universityId, Long branchId);

    long countByCollege_IdAndBranch_IdAndActiveTrue(Long collegeId, Long branchId);

    @Query("""
            select p.user.id
              from UserAcademicProfile p
             where p.university.id = :universityId
               and p.active = true
            """)
    List<Long> findUserIdsByUniversityId(@Param("universityId") Long universityId);
}
