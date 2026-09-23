package com.edunest.backend.modules.communitychat.entity;
import java.time.LocalDateTime;
import com.edunest.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="community_message_reports",uniqueConstraints=@UniqueConstraint(name="uk_community_report_user_message",columnNames={"message_id","reporter_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityMessageReport {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="message_id",nullable=false) private CommunityMessage message;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="reporter_id",nullable=false) private User reporter;
 @Column(nullable=false,length=1000) private String reason;
 @Column(nullable=false) private LocalDateTime createdAt;
 @Column(nullable=false) private boolean resolved;
}