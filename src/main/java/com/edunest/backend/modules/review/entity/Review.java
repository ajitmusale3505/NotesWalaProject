package com.edunest.backend.modules.review.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_review_user_resource",
                columnNames = {"user_id", "resource_id"}
        ),
        indexes = {
                @Index(name = "idx_review_resource_created", columnList = "resource_id, created_at"),
                @Index(name = "idx_review_user_resource", columnList = "user_id, resource_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String review;
}
