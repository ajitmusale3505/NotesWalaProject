package com.edunest.backend.modules.communitychat.repository;

import com.edunest.backend.modules.communitychat.entity.CommunityMessage;
import com.edunest.backend.modules.communitychat.entity.CommunityMessageStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityMessageRepository extends JpaRepository<CommunityMessage, Long> {

    interface AuthorMessageCount {
        Long getUserId();
        Long getCountValue();
    }

    @Query("""
        select m.author.id as userId, count(m.id) as countValue
          from CommunityMessage m
         where m.channel.id = :channelId
           and m.author.id in :userIds
           and m.status = com.edunest.backend.modules.communitychat.entity.CommunityMessageStatus.ACTIVE
         group by m.author.id
        """)
    List<AuthorMessageCount> countActiveMessagesByAuthors(
            @Param("channelId") Long channelId,
            @Param("userIds") Collection<Long> userIds);

    long countByChannel_IdAndStatus(Long channelId, CommunityMessageStatus status);

    @EntityGraph(attributePaths = {"author", "channel"})
    Page<CommunityMessage> findByChannel_IdAndStatusOrderByCreatedAtDesc(
            Long channelId, CommunityMessageStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "channel"})
    Page<CommunityMessage> findByChannel_IdAndIdLessThanAndStatusOrderByIdDesc(
            Long channelId, Long beforeId, CommunityMessageStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "channel"})
    Optional<CommunityMessage> findFirstByChannel_IdAndPinnedTrueAndStatusOrderByCreatedAtDesc(
            Long channelId, CommunityMessageStatus status);

    Optional<CommunityMessage> findByIdAndChannel_Id(Long id, Long channelId);

    Optional<CommunityMessage> findByChannel_IdAndAuthor_IdAndClientMessageId(
            Long channelId, Long authorId, String clientMessageId);

    long countByAuthor_IdAndCreatedAtAfter(Long userId, LocalDateTime since);

    @Query("""
        select m from CommunityMessage m
        where m.channel.id = :channelId
          and m.parentMessage.id = :parentId
          and m.status = com.edunest.backend.modules.communitychat.entity.CommunityMessageStatus.ACTIVE
        order by m.createdAt asc
        """)
    Page<CommunityMessage> findThread(
            @Param("channelId") Long channelId,
            @Param("parentId") Long parentId,
            Pageable pageable);
}
