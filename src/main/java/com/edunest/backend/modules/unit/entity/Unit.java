package com.edunest.backend.modules.unit.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "syllabus_units",
        uniqueConstraints = @UniqueConstraint(name = "uk_syllabus_units_subject_number",
                columnNames = {"subject_id", "unit_number"}),
        indexes = {
                @Index(name = "idx_syllabus_units_subject", columnList = "subject_id"),
                @Index(name = "idx_syllabus_units_subject_active", columnList = "subject_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_number", nullable = false)
    private Integer unitNumber;

    @Column(name = "chapter_name", nullable = false, length = 200)
    private String chapterName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}