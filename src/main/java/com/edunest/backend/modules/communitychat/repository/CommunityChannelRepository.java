package com.edunest.backend.modules.communitychat.repository;

import com.edunest.backend.modules.communitychat.entity.CommunityChannel;
import com.edunest.backend.modules.communitychat.entity.CommunityChannelType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityChannelRepository extends JpaRepository<CommunityChannel, Long> {

    Optional<CommunityChannel> findByChannelTypeAndCollege_IdAndBranch_Id(
            CommunityChannelType type, Long collegeId, Long branchId);

    @EntityGraph(attributePaths = {"university", "college", "branch"})
    Optional<CommunityChannel> findByChannelTypeAndUniversity_Id(
            CommunityChannelType type, Long universityId);

    @EntityGraph(attributePaths = {"university", "college", "branch"})
    Optional<CommunityChannel> findByChannelTypeAndUniversity_IdAndBranch_Id(
            CommunityChannelType type, Long universityId, Long branchId);

    @Modifying
    @Query(value = """
        INSERT INTO community_channels (channel_type, college_id, branch_id, university_id, active)
        VALUES ('UNIVERSITY_BRANCH', NULL, :branchId, :universityId, TRUE)
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    void insertUniversityBranchChannelIfAbsent(
            @Param("universityId") Long universityId,
            @Param("branchId") Long branchId);

    @Modifying
    @Query(value = """
        INSERT INTO community_channels (channel_type, college_id, branch_id, university_id, active)
        VALUES ('BRANCH', :collegeId, :branchId, NULL, TRUE)
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    void insertCollegeBranchChannelIfAbsent(
            @Param("collegeId") Long collegeId,
            @Param("branchId") Long branchId);

    // Kept for legacy admin tooling; student authorization rejects UNIVERSITY-only rooms.
    @Modifying
    @Query(value = """
        INSERT INTO community_channels (channel_type, college_id, branch_id, university_id, active)
        VALUES ('UNIVERSITY', NULL, NULL, :universityId, TRUE)
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    void insertUniversityChannelIfAbsent(@Param("universityId") Long universityId);
}
