package com.edunest.backend.modules.program.entity;

import com.edunest.backend.common.entity.BaseEntity;
import com.edunest.backend.common.id.BusinessIdGenerator;
import com.edunest.backend.modules.university.entity.University;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "programs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_program_university_code", columnNames = {"university_id", "code"})
        },
        indexes = {
                @Index(name = "idx_program_university_active", columnList = "university_id, active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program extends BaseEntity {

    @Id
    @Column(length = 20, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(name = "degree_level", nullable = false, length = 30)
    private String degreeLevel;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @PrePersist
    private void generateId() {
        if (id == null) {
            throw new IllegalStateException(
                    "Program ID must be assigned by the application service before persistence");
        }
    }
}
