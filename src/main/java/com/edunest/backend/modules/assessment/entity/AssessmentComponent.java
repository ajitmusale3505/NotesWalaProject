package com.edunest.backend.modules.assessment.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.subject.entity.SubjectOffering;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessment_components",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_assessment_offering_type",
                        columnNames = {"subject_offering_id", "type_id", "component_key"})
        },
        indexes = {
                @Index(name = "idx_assessment_offering_order",
                        columnList = "subject_offering_id, display_order"),
                @Index(name = "idx_assessment_offering_active",
                        columnList = "subject_offering_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentComponent extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_offering_id", nullable = false)
    private SubjectOffering subjectOffering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    private AssessmentComponentType type;

    /**
     * Allows multiple components of the same type when a curriculum requires it.
     * Examples: INSEM-1, INSEM-2, PRACTICAL-MANUAL, PRACTICAL-CODE.
     */
    @Column(name = "component_key", nullable = false, length = 50)
    private String componentKey;

    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @Column(name = "passing_marks")
    private Integer passingMarks;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private boolean includedInTotal;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "counts_toward_final_total", nullable = false)
    private boolean countsTowardFinalTotal;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (id == null) {
            throw new IllegalStateException("Assessment component ID must be assigned as ASM#####");
        }
        if (maxMarks == null || maxMarks < 0) {
            throw new IllegalStateException("Maximum marks cannot be negative");
        }
        if (passingMarks != null && (passingMarks < 0 || passingMarks > maxMarks)) {
            throw new IllegalStateException("Passing marks must be between 0 and maximum marks");
        }
        if (displayOrder == null || displayOrder < 1) {
            throw new IllegalStateException("Display order must be positive");
        }
    }
}
