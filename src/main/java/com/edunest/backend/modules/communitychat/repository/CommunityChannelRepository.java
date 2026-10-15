package com.edunest.backend.modules.communitychat.repository;

import com.edunest.backend.modules.communitychat.entity.CommunityChannel;
import com.edunest.backend.modules.communitychat.entity.CommunityChannelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityChannelRepository extends JpaRepository<CommunityChannel, Long> {

    Optional<CommunityChannel> findByChannelTypeAndCollege_IdAndBranch_Id(
            CommunityChannelType type, Long collegeId, Long branchId);

    Optional<CommunityChannel> findByChannelTypeAndCollege_IdAndBranch_IdAndSemester_Id(
            CommunityChannelType type, Long collegeId, Long branchId, Long semesterId);

    @EntityGraph(attributePaths = {"university", "college", "branch", "semester"})
    Optional<CommunityChannel> findByChannelTypeAndUniversity_Id(
            CommunityChannelType type, Long universityId);

    @EntityGraph(attributePaths = {"university", "college", "branch", "semester"})
    Optional<CommunityChannel> findByChannelTypeAndUniversity_IdAndBranch_Id(
            CommunityChannelType type, Long universityId, Long branchId);

    @EntityGraph(attributePaths = {"university", "college", "branch", "semester"})
    Optional<CommunityChannel> findByChannelTypeAndUniversity_IdAndBranch_IdAndSemester_Id(
            CommunityChannelType type, Long universityId, Long branchId, Long semesterId);

    @EntityGraph(attributePaths = {"university", "college", "branch", "semester"})
    Optional<CommunityChannel> findByIdAndChannelType(Long id, CommunityChannelType type);

    @Query("""
        select c from CommunityChannel c
        left join c.university u
        left join c.branch b
        left join c.semester s
        where c.channelType = :channelType
          and s.id is not null
          and (:universityId is null or u.id = :universityId)
          and (:active is null or c.active = :active)
          and (
              :query is null
              or lower(coalesce(c.name, '')) like concat('%', lower(:query), '%')
              or lower(coalesce(u.name, '')) like concat('%', lower(:query), '%')
              or lower(coalesce(b.name, '')) like concat('%', lower(:query), '%')
              or lower(coalesce(s.name, '')) like concat('%', lower(:query), '%')
          )
        order by c.createdAt desc, c.id desc
        """)
    @EntityGraph(attributePaths = {"university", "branch", "semester"})
    Page<CommunityChannel> searchAdminGroups(
            @Param("channelType") CommunityChannelType channelType,
            @Param("universityId") Long universityId,
            @Param("active") Boolean active,
            @Param("query") String query,
            Pageable pageable);

    interface CommunityGroupMemberCount {
        Long getChannelId();
        Long getMemberCount();
    }

    @Query("""
        select c.id as channelId, count(p.id) as memberCount
        from CommunityChannel c
        left join com.edunest.backend.modules.userprofile.entity.UserAcademicProfile p
          on p.university.id = c.university.id
         and p.branch.id = c.branch.id
         and p.currentSemester.id = c.semester.id
         and p.active = true
        where c.id in :ids
        group by c.id
        """)
    List<CommunityGroupMemberCount> countMembersForGroupIds(@Param("ids") Collection<Long> ids);

    long countByChannelType(CommunityChannelType type);
    long countByChannelTypeAndActiveTrue(CommunityChannelType type);
    long countByChannelTypeAndSemesterIsNotNull(CommunityChannelType type);
    long countByChannelTypeAndSemesterIsNotNullAndActiveTrue(CommunityChannelType type);

    @Modifying
    @Query(value = """
        INSERT INTO community_channels
            (channel_type, college_id, branch_id, university_id, semester_id, name, active, created_at, updated_at)
        VALUES ('BRANCH', :collegeId, :branchId, NULL, :semesterId, :name, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    void insertCollegeBranchChannelIfAbsent(
            @Param("collegeId") Long collegeId,
            @Param("branchId") Long branchId,
            @Param("semesterId") Long semesterId,
            @Param("name") String name);
}
