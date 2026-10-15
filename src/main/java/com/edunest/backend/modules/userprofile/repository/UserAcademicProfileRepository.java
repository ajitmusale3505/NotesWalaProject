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
    Page<UserAcademicProfile> findByUniversity_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
            Long universityId, Long branchId, Long semesterId, Pageable pageable);

    @Query("""
            select p
              from UserAcademicProfile p
              join p.user u
              left join p.branch b
              left join p.currentSemester s
             where p.university.id = :universityId
               and p.branch.id = :branchId
               and p.currentSemester.id = :semesterId
               and (:status = 'all' or (:status = 'active' and p.active = true)
                    or (:status = 'inactive' and p.active = false))
               and (
                    :query = ''
                    or lower(coalesce(u.fullName, '')) like concat('%', lower(:query), '%')
                    or lower(coalesce(u.email, '')) like concat('%', lower(:query), '%')
                    or lower(coalesce(p.rollNumber, '')) like concat('%', lower(:query), '%')
                    or lower(coalesce(coalesce(p.phoneNumber, u.phoneNumber), '')) like concat('%', lower(:query), '%')
               )
               and (
                    :activityLevel = 'all'
                    or (:activityLevel = 'none' and
                        (select count(r.id) from CommunityMessageReport r
                          where r.message.channel.id = :channelId
                            and r.message.author.id = p.user.id) = 0)
                    or (:activityLevel = 'low' and
                        (select count(r.id) from CommunityMessageReport r
                          where r.message.channel.id = :channelId
                            and r.message.author.id = p.user.id) between 1 and 2)
                    or (:activityLevel = 'medium' and
                        (select count(r.id) from CommunityMessageReport r
                          where r.message.channel.id = :channelId
                            and r.message.author.id = p.user.id) between 3 and 4)
                    or (:activityLevel = 'high' and
                        (select count(r.id) from CommunityMessageReport r
                          where r.message.channel.id = :channelId
                            and r.message.author.id = p.user.id) >= 5)
               )
             order by p.createdAt desc, p.id desc
            """)
    @EntityGraph(attributePaths = {"user", "university", "branch", "currentSemester"})
    Page<UserAcademicProfile> searchAdminGroupMembers(
            @Param("channelId") Long channelId,
            @Param("universityId") Long universityId,
            @Param("branchId") Long branchId,
            @Param("semesterId") Long semesterId,
            @Param("query") String query,
            @Param("status") String status,
            @Param("activityLevel") String activityLevel,
            Pageable pageable);

    @EntityGraph(attributePaths = {"user", "branch", "currentSemester"})
    Page<UserAcademicProfile> findByCollege_IdAndBranch_IdAndActiveTrue(
            Long collegeId, Long branchId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "branch", "currentSemester"})
    Page<UserAcademicProfile> findByCollege_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
            Long collegeId, Long branchId, Long semesterId, Pageable pageable);

    long countByUniversity_IdAndActiveTrue(Long universityId);

    long countByUniversity_IdAndBranch_IdAndActiveTrue(Long universityId, Long branchId);

    long countByUniversity_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
            Long universityId, Long branchId, Long semesterId);

    long countByUniversity_IdAndBranch_IdAndCurrentSemester_Id(
            Long universityId, Long branchId, Long semesterId);

    long countByCollege_IdAndBranch_IdAndActiveTrue(Long collegeId, Long branchId);

    long countByCollege_IdAndBranch_IdAndCurrentSemester_IdAndActiveTrue(
            Long collegeId, Long branchId, Long semesterId);

    @Query("""
            select count(p.id)
            from UserAcademicProfile p
            where p.active = true
              and exists (
                select c.id from com.edunest.backend.modules.communitychat.entity.CommunityChannel c
                where c.channelType = com.edunest.backend.modules.communitychat.entity.CommunityChannelType.UNIVERSITY_BRANCH
                  and c.university.id = p.university.id
                  and c.branch.id = p.branch.id
                  and c.semester.id = p.currentSemester.id
              )
            """)
    long countActiveMembersMappedToUniversityBranchGroups();

    @Query("""
            select p.user.id
              from UserAcademicProfile p
             where p.university.id = :universityId
               and p.active = true
            """)
    List<Long> findUserIdsByUniversityId(@Param("universityId") Long universityId);
}
