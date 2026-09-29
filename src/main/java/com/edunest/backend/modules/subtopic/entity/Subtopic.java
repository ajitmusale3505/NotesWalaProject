package com.edunest.backend.modules.subtopic.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "syllabus_subtopics",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_syllabus_subtopics_topic_number",
                columnNames = {"topic_id", "subtopic_number"}),
        indexes = {
                @Index(name = "idx_syllabus_subtopics_topic_active",
                        columnList = "topic_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subtopic extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @Column(name = "subtopic_number", nullable = false)
    private Integer subtopicNumber;

    @Column(nullable = false, length = 400)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (id == null || !id.matches("STP\\d{5,}")) {
            throw new IllegalStateException("Subtopic ID must be assigned as STP#####");
        }
        if (subtopicNumber == null || subtopicNumber < 1) {
            throw new IllegalStateException("Subtopic number must be positive");
        }
    }
}
