package com.edunest.backend.modules.communitychat.entity;
import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.college.entity.College;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="community_channels",indexes=@Index(name="idx_community_channel_type",columnList="channel_type"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityChannel {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Enumerated(EnumType.STRING) @Column(name="channel_type",nullable=false,length=20) private CommunityChannelType channelType;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="college_id",nullable=false) private College college;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="branch_id") private Branch branch;
 @Column(nullable=false) private boolean active;
}