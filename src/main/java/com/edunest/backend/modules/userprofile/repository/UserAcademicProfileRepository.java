package com.edunest.backend.modules.userprofile.repository;

import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface UserAcademicProfileRepository extends JpaRepository<UserAcademicProfile, Long> {

    @EntityGraph(attributePaths = {
            "user", "university", "college", "branch", "academicYear", "currentSemester"
    })
    Optional<UserAcademicProfile> findByUserIdAndActiveTrue(Long userId);

    @EntityGraph(attributePaths = {
            "user", "university", "college", "branch", "academicYear", "currentSemester"
    })
    Optional<UserAcademicProfile> findByUserId(Long userId);

    @Query("""
            select p.user.id
              from UserAcademicProfile p
             where p.university.id = :universityId
               and p.active = true
            """)
    List<Long> findUserIdsByUniversityId(@Param("universityId") Long universityId);
}
