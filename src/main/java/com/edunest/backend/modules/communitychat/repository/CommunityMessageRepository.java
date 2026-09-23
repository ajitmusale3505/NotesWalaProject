package com.edunest.backend.modules.communitychat.repository;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.edunest.backend.modules.communitychat.entity.CommunityMessage;
public interface CommunityMessageRepository extends JpaRepository<CommunityMessage,Long> {
 @EntityGraph(attributePaths={"author","channel"})
 Page<CommunityMessage> findByChannel_IdAndStatusOrderByCreatedAtDesc(Long channelId,com.edunest.backend.modules.communitychat.entity.CommunityMessageStatus status,Pageable pageable);
 Optional<CommunityMessage> findByIdAndChannel_Id(Long id,Long channelId);
 long countByAuthor_IdAndCreatedAtAfter(Long userId,java.time.LocalDateTime since);
 @Query("select m from CommunityMessage m where m.channel.id=:channelId and m.parentMessage.id=:parentId and m.status=com.edunest.backend.modules.communitychat.entity.CommunityMessageStatus.ACTIVE order by m.createdAt asc")
 Page<CommunityMessage> findThread(@Param("channelId") Long channelId,@Param("parentId") Long parentId,Pageable pageable);
}