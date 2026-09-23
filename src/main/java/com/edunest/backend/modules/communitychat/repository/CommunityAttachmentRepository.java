package com.edunest.backend.modules.communitychat.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.edunest.backend.modules.communitychat.entity.CommunityMessageAttachment;
public interface CommunityAttachmentRepository extends JpaRepository<CommunityMessageAttachment,Long> {}