package com.edunest.backend.modules.exampattern.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.modules.university.entity.University;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_patterns",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_exam_pattern_university_code", columnNames = {"university_id", "code"})
        },
        indexes = {
                @Index(name = "idx_exam_pattern_university_active", columnList = "university_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamPattern extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(name = "effective_from")
    private Integer effectiveFromYear;

    @Column(name = "effective_to")
    private Integer effectiveToYear;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @PrePersist
    private void validateId() {
        if (id == null) {
            throw new IllegalStateException(
                    "Exam pattern ID must be assigned by the application service before persistence");
        }
    }
}
