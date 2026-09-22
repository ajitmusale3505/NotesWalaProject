package com.edunest.backend.modules.userprofile.repository;

import com.edunest.backend.modules.userprofile.entity.UserAcademicProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

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
}