package com.edunest.backend.modules.resourceanalytics.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.common.enums.AnalyticsEventType;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resource_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceAnalytics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalyticsEventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}