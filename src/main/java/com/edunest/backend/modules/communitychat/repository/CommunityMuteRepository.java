package com.edunest.backend.modules.communitychat.repository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edunest.backend.modules.communitychat.entity.CommunityUserMute;
public interface CommunityMuteRepository extends JpaRepository<CommunityUserMute,Long> {
 Optional<CommunityUserMute> findByChannel_IdAndUser_Id(Long channelId,Long userId);
 boolean existsByChannel_IdAndUser_IdAndExpiresAtAfter(Long channelId,Long userId,LocalDateTime now);
}