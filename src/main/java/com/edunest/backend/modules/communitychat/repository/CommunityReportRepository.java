package com.edunest.backend.modules.communitychat.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.edunest.backend.modules.communitychat.entity.CommunityMessageReport;
public interface CommunityReportRepository extends JpaRepository<CommunityMessageReport,Long> {
 interface AuthorReportCount {
  Long getUserId();
  Long getCountValue();
 }
 @Query("""
  select r.message.author.id as userId, count(r.id) as countValue
    from CommunityMessageReport r
   where r.message.channel.id = :channelId
     and r.message.author.id in :userIds
   group by r.message.author.id
  """)
 List<AuthorReportCount> countReportsByAuthors(
  @Param("channelId") Long channelId, @Param("userIds") Collection<Long> userIds);
 @Query("""
  select count(distinct r.message.author.id)
    from CommunityMessageReport r
   where r.message.channel.id = :channelId
  """)
 long countDistinctReportedAuthorsByChannelId(@Param("channelId") Long channelId);
 boolean existsByMessage_IdAndReporter_Id(Long messageId,Long reporterId);
 Page<CommunityMessageReport> findByResolvedFalseOrderByCreatedAtAsc(Pageable pageable);
}