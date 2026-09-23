package com.edunest.backend.modules.communitychat.entity;
import java.time.LocalDateTime;
import com.edunest.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="community_user_mutes",uniqueConstraints=@UniqueConstraint(name="uk_community_mute_channel_user",columnNames={"channel_id","user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityUserMute {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="channel_id",nullable=false) private CommunityChannel channel;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private User user;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="muted_by",nullable=false) private User mutedBy;
 @Column(nullable=false) private LocalDateTime expiresAt;
}