package com.edunest.backend.modules.curriculum.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.semester.entity.Semester;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curriculum_semesters",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_curriculum_semester",
                        columnNames = {"curriculum_id", "semester_id"})
        },
        indexes = {
                @Index(name = "idx_curriculum_semester_lookup",
                        columnList = "curriculum_id, study_year, display_order")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumSemester extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(name = "study_year", nullable = false)
    private Integer studyYear;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    private void validateId() {
        if (id == null || !id.matches("CSEM\\d{5,}")) {
            throw new IllegalStateException("Curriculum Semester ID must be assigned as CSEM#####");
        }
        if (studyYear == null || studyYear < 1 || studyYear > 4) {
            throw new IllegalStateException("Study year must be between 1 and 4");
        }
        if (displayOrder == null || displayOrder < 1) {
            throw new IllegalStateException("Display order must be positive");
        }
    }
}
