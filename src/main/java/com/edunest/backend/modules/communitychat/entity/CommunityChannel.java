package com.edunest.backend.modules.communitychat.entity;

import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.university.entity.University;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_channels",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_community_uni_branch_channel",
                        columnNames = {"channel_type", "university_id", "branch_id"}),
                @UniqueConstraint(name = "uk_community_college_branch_channel",
                        columnNames = {"channel_type", "college_id", "branch_id"})
        },
        indexes = @Index(name = "idx_community_channel_type", columnList = "channel_type"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 20)
    private CommunityChannelType channelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    @Column(nullable = false)
    private boolean active;
}
