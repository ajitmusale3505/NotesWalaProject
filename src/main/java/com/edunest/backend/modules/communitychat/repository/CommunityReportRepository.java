package com.edunest.backend.modules.communitychat.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edunest.backend.modules.communitychat.entity.CommunityMessageReport;
public interface CommunityReportRepository extends JpaRepository<CommunityMessageReport,Long> {
 boolean existsByMessage_IdAndReporter_Id(Long messageId,Long reporterId);
}