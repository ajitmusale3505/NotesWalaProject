package com.edunest.backend.modules.subject.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.curriculum.entity.CurriculumSemester;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject_offerings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_subject_offering_curriculum_semester_code",
                        columnNames = {"curriculum_semester_id", "subject_id", "code"})
        },
        indexes = {
                @Index(name = "idx_subject_offering_curriculum_semester_active",
                        columnList = "curriculum_semester_id, active"),
                @Index(name = "idx_subject_offering_subject_active",
                        columnList = "subject_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectOffering extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    /**
     * Legacy Subject PK is retained during the staged migration.
     * The Subject.businessId field is the stable public/application identity.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curriculum_semester_id", nullable = false)
    private CurriculumSemester curriculumSemester;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false)
    private Integer credits;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private SubjectCategory category;

    @Column(nullable = false)
    private boolean mandatory;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    private void validateId() {
        if (id == null || !id.matches("SOF\\d{5,}")) {
            throw new IllegalStateException("Subject offering ID must be assigned as SOF#####");
        }
        if (credits == null || credits < 0) {
            throw new IllegalStateException("Credits cannot be negative");
        }
    }
}
