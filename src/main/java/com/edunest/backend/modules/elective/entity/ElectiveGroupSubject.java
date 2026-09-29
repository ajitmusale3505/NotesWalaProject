package com.edunest.backend.modules.elective.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.subject.entity.SubjectOffering;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "elective_group_subjects",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_elective_group_subject",
                columnNames = {"elective_group_id", "subject_offering_id"}),
        indexes = {
                @Index(name = "idx_elective_group_subject_order",
                        columnList = "elective_group_id, display_order"),
                @Index(name = "idx_elective_group_subject_offering",
                        columnList = "subject_offering_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectiveGroupSubject extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "elective_group_id", nullable = false)
    private ElectiveGroup electiveGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_offering_id", nullable = false)
    private SubjectOffering subjectOffering;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (id == null || !id.matches("ELG_SUB\\d{5,}")) {
            throw new IllegalStateException(
                    "Elective group subject ID must be assigned as ELG_SUB#####");
        }
        if (displayOrder == null || displayOrder < 1) {
            throw new IllegalStateException("Display order must be positive");
        }
    }
}
