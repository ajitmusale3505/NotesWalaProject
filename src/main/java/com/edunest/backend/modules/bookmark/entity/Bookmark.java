package com.edunest.backend.modules.bookmark.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.resource.entity.Resource;
import com.edunest.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "bookmarks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_bookmark_user_resource",
                columnNames = {"user_id", "resource_id"}
        ),
        indexes = {
                @Index(name = "idx_bookmark_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_bookmark_resource", columnList = "resource_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;
}
