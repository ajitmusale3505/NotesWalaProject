package com.edunest.backend.modules.communitychat.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edunest.backend.modules.communitychat.entity.*;
public interface CommunityChannelRepository extends JpaRepository<CommunityChannel,Long> {
 Optional<CommunityChannel> findByChannelTypeAndCollege_IdAndBranch_Id(CommunityChannelType type,Long collegeId,Long branchId);
}