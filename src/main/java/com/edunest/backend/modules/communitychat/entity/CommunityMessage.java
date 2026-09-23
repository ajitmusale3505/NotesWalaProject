package com.edunest.backend.modules.communitychat.entity;
import java.time.LocalDateTime;
import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="community_messages",indexes={
 @Index(name="idx_community_message_channel_created",columnList="channel_id,created_at"),
 @Index(name="idx_community_message_parent",columnList="parent_message_id")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityMessage extends BaseEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="channel_id",nullable=false) private CommunityChannel channel;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="author_id",nullable=false) private User author;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="parent_message_id") private CommunityMessage parentMessage;
 @Column(nullable=false,length=2000) private String content;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private CommunityMessageStatus status;
 @Column(nullable=false) private boolean pinned;
 private LocalDateTime deletedAt;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="deleted_by") private User deletedBy;
}