package com.edunest.backend.modules.communitychat.entity;

import com.edunest.backend.modules.branch.entity.Branch;
import com.edunest.backend.modules.college.entity.College;
import com.edunest.backend.modules.semester.entity.Semester;
import com.edunest.backend.modules.university.entity.University;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "community_channels",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_community_uni_branch_semester_channel",
                        columnNames = {"channel_type", "university_id", "branch_id", "semester_id"}),
                @UniqueConstraint(name = "uk_community_college_branch_semester_channel",
                        columnNames = {"channel_type", "college_id", "branch_id", "semester_id"})
        },
        indexes = {
                @Index(name = "idx_community_channel_type", columnList = "channel_type"),
                @Index(name = "idx_community_channel_university_branch_semester", columnList = "university_id,branch_id,semester_id")
        })
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "icon_url", length = 1000)
    private String iconUrl;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}
