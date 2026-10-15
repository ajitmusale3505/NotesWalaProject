package com.edunest.backend.modules.communitychat.repository;

import com.edunest.backend.modules.communitychat.entity.CommunityMessageAttachment;
import com.edunest.backend.modules.communitychat.entity.CommunityMessageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityAttachmentRepository extends JpaRepository<CommunityMessageAttachment, Long> {

    @Query("""
        select a from CommunityMessageAttachment a
        join fetch a.message m
        join fetch m.author
        join fetch m.channel c
        where c.id = :channelId and m.status = :status
        order by m.createdAt desc
        """)
    List<CommunityMessageAttachment> findRecentByChannelId(
            @Param("channelId") Long channelId,
            @Param("status") CommunityMessageStatus status,
            Pageable pageable);
}
