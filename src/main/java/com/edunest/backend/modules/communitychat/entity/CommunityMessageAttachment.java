package com.edunest.backend.modules.communitychat.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="community_message_attachments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityMessageAttachment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="message_id",nullable=false) private CommunityMessage message;
 @Column(nullable=false,length=500) private String storageKey;
 @Column(nullable=false,length=255) private String fileName;
 @Column(length=100) private String contentType;
 @Column(nullable=false) private long sizeBytes;
}