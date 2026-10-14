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

    @Modifying
    @Query(value = """
        INSERT INTO community_channels (channel_type, college_id, branch_id, university_id, active)
        VALUES ('UNIVERSITY', NULL, NULL, :universityId, TRUE)
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    void insertUniversityChannelIfAbsent(@Param("universityId") Long universityId);
}
